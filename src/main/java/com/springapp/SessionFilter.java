package com.springapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionFilter implements Filter {

    private static Logger logger= LoggerFactory.getLogger(SessionFilter.class);

    private SessionManager sessionManager;

    private ServletContext context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.context = filterConfig.getServletContext();
        this.context.log("SessionFilter initialized");
        WebApplicationContext springContext =
                WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        sessionManager = (SessionManager)springContext.getBean("sessionManager");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session=request.getSession(false);
        if(session==null){
            session=request.getSession();// create new session id if not exist.
            logger.info("filter: create new session_id:" + session.getId());

            String sessionIdFromRequest=this.getJSESSIONIDFromRequest(request.getCookies());
            if(sessionIdFromRequest!=null){
                logger.info("trying to restore session with user submitted JsessionId:"+sessionIdFromRequest);
                sessionManager.restoreRedisToSession(session, sessionIdFromRequest);
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }



    private String getJSESSIONIDFromRequest(Cookie[] cookies){
        if (cookies != null)
        {
            for (Cookie cook : cookies)
            {
                if ("JSESSIONID".equalsIgnoreCase(cook.getName()))
                {
                    return cook.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {

    }

}
