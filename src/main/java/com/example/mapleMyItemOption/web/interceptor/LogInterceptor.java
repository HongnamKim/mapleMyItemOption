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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(request.getMethod().equals("POST")){
            String uuid = UUID.randomUUID().toString();
            request.setAttribute("uuid", uuid);

            logRequest(request, uuid, false);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        if(request.getMethod().equals("POST")){
            String uuid = (String) request.getAttribute("uuid");
            logRequest(request, uuid, true);
        }

        if (ex != null){
            log.error("Search Error", ex);
            throw ex;
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
