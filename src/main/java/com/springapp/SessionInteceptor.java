package com.springapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Component
public class SessionInteceptor extends HandlerInterceptorAdapter {

    private static Logger logger= LoggerFactory.getLogger(SessionInteceptor.class);

    @Resource
    private SessionManager sessionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {

        HttpSession session=request.getSession(false);
        if(session==null){
            logger.info("skip session backup: session is null");
        }
        if(session!=null){
            sessionManager.backupSessionToRedis(session);
        }

    }

}
