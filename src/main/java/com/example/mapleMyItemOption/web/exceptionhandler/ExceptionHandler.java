package com.example.mapleMyItemOption.web.exceptionhandler;

import com.example.mapleMyItemOption.api.Error;
import com.example.mapleMyItemOption.api.ErrorMessage;
import com.example.mapleMyItemOption.exceptions.IllegalDateException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.net.UnknownHostException;


@Slf4j
public class ExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        if (ex instanceof HttpClientErrorException httpException) { // 서버 - 넥슨 간 문제

            String uuid = (String) request.getAttribute("uuid");
            ErrorMessage errorMessage = httpException.getResponseBodyAs(ErrorMessage.class);

            assert errorMessage != null;
            Error error = errorMessage.getError();

            if(error.getMessage().contains("01")){
                log.error("NEXON SERVER ERROR");
                response.setStatus(HttpStatus.BAD_GATEWAY.value());
                return new ModelAndView("error/502");
            }

            log.info("{} [{}] {}", error.getName(), uuid, error.getMessage());

            response.setStatus(httpException.getStatusCode().value());

            return new ModelAndView("error/404");

        } else if (ex instanceof IllegalDateException exception) { // 날짜 선택 관련 문제

            String uuid = (String) request.getAttribute("uuid");
            log.info("SERVICE0001 [{}][{}][{}] {}", uuid, exception.getCharacterName(), exception.getDate(), exception.getMessage());

            response.setStatus(HttpStatus.BAD_REQUEST.value());

            return new ModelAndView("error/400");

        } else if (ex instanceof UnknownHostException || ex instanceof ResourceAccessException){ // 서버 인터넷 통신 문제

            log.error("INTERNET CONNECTION FAILURE");

            return new ModelAndView("error/500");
        } else {
            response.setStatus(500);
            log.error("Internal ERROR", ex);
            return new ModelAndView("error/500");
        }

        //return null;
    }
}
