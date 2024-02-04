package com.example.mapleMyItemOption.web.argumentResolver;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class CharacterResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasAnnotation = parameter.hasParameterAnnotation(SessionCharacter.class);
        boolean isCharacterType = Character.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && isCharacterType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        HttpSession session = request.getSession(false);

        if(session == null){
            return null;
        }

        return session.getAttribute(SessionConst.CHARACTER);
    }
}
