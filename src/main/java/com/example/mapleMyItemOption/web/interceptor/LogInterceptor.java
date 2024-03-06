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

    public static final String UUID = "uuid";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if("POST".equals(request.getMethod())){
            String uuid = java.util.UUID.randomUUID().toString();
            request.setAttribute(UUID, uuid);

            logRequest(request, uuid, false);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if("POST".equals(request.getMethod())){
            String uuid = (String) request.getAttribute(UUID);
            log.info("SUCCESS [{}]", uuid);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        if (ex != null){
            log.error(ex.getMessage());
        }

        if("POST".equals(request.getMethod())){
            String uuid = (String) request.getAttribute(UUID);
            logRequest(request, uuid, true);
        }


    }

    private void logRequest(HttpServletRequest request, String uuid, Boolean completion) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] characterName = parameterMap.get("characterName");
        String[] dates = parameterMap.get("date");
        String[] maxDate = parameterMap.get("maximumAssaultDate");

        if(completion){
            log.info("RESPONSE [{}][{}][{}][{}]", uuid, characterName[0], dates[0], maxDate != null);
        } else {
            log.info("REQUEST [{}][{}][{}][{}]", uuid, characterName[0], dates[0], maxDate != null);
        }
    }
}
