package com.springapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class SessionManager {

    private static Logger logger= LoggerFactory.getLogger(SessionManager.class);

    public static final String REDIS_BACKUP_TIMESTAMP ="REDIS_BACKUP_TIMESTAMP";

    public static int REDIS_TTL=5;
    public static TimeUnit REDIS_TTL_UNIT=TimeUnit.MINUTES;

    @Resource
    public RedisTemplate redisTemplate;

    public void backupSessionToRedis(HttpSession session){
        String sessionId=session.getId();
        if(!isSessionContainsNewTimestamp(session, sessionId)){
            logger.info("skip copying to redis; session is older than redis");
            return;
        }

        String attributeKey=null;
        HashOperations<String,String,Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(sessionId, REDIS_BACKUP_TIMESTAMP, session.getLastAccessedTime());
        Enumeration<String> items = session.getAttributeNames();
        int count=0;
        while(items.hasMoreElements()){
            attributeKey=items.nextElement();
            hashOperations.put(sessionId, attributeKey, session.getAttribute(attributeKey));
            count++;
        }

        redisTemplate.expire(sessionId, REDIS_TTL, REDIS_TTL_UNIT);

        if(count==0){
            logger.info("no session attributes("+count+" items) to copy to redis");
        }else{
            logger.info("session attributes("+count+" items) copied to redis for key: "+sessionId+" (expire in "+REDIS_TTL+" "+REDIS_TTL_UNIT+")");
        }

        Map<String,Object> redisMap=hashOperations.entries(sessionId);
        Iterator<String> items2= redisMap.keySet().iterator();
        while(items2.hasNext()){
            attributeKey=items2.next();
            logger.debug(" redis debug: key:" + attributeKey + ", value:" + redisMap.get(attributeKey));
        }


    }

    public void restoreRedisToSession(HttpSession session, String redisKey){
        String attributeKey=null;
        HashOperations<String,String,Object> hashOperations = redisTemplate.opsForHash();
        Map<String,Object> redisMap=hashOperations.entries(redisKey);
        Iterator<String> items= redisMap.keySet().iterator();
        while(items.hasNext()){
            attributeKey=items.next();
            logger.debug(" key:" + attributeKey + ", value:" + redisMap.get(attributeKey));
            session.setAttribute(attributeKey,redisMap.get(attributeKey));
        }

        logger.info("filter: session attributes("+redisMap.size()+" items) recovered from  redis");
    }


    public boolean isSessionContainsNewTimestamp(HttpSession session, String redisKey){
        HashOperations<String,String,Object> hashOperations = redisTemplate.opsForHash();
        Long redisTimestamp = (Long)hashOperations.get(redisKey, REDIS_BACKUP_TIMESTAMP);
        if(redisTimestamp==null){
            return true;
        }
        return session.getLastAccessedTime() >redisTimestamp;

    }

}
