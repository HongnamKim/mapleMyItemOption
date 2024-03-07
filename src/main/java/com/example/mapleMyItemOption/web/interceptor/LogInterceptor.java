package com.example.mapleMyItemOption.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if("POST".equals(request.getMethod())){
            String uuid = UUID.randomUUID().toString();
            request.setAttribute(LOG_ID, uuid);

            logRequest(request, LogType.REQUEST);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if("POST".equals(request.getMethod())){
            logRequest(request, LogType.SUCCESS);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        if (ex != null){
            log.error(ex.getMessage());
        }

        if("POST".equals(request.getMethod())){
            logRequest(request, LogType.RESPONSE);
        }
    }

    private void logRequest(HttpServletRequest request, LogType logType) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] characterName = parameterMap.get("characterName");
        String[] dates = parameterMap.get("date");
        String[] maxDate = parameterMap.get("maximumAssaultDate");

        String uuid =  (String)request.getAttribute(LOG_ID);

        switch(logType){
            case REQUEST -> log.info("REQUEST [{}][{}][{}][{}]", uuid, characterName[0], dates[0], maxDate != null);
            case SUCCESS -> log.info("SUCCESS [{}][{}][{}][{}]", uuid, characterName[0], dates[0], maxDate != null);
            case RESPONSE -> log.info("RESPONSE [{}][{}][{}][{}]", uuid, characterName[0], dates[0], maxDate != null);
        }
    }
}
