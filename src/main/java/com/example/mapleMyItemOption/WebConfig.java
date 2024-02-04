package com.example.mapleMyItemOption;

import com.example.mapleMyItemOption.api.ApiService;
import com.example.mapleMyItemOption.api.ExternalApiService;
import com.example.mapleMyItemOption.api.InternalApiService;
import com.example.mapleMyItemOption.web.argumentResolver.CharacterResolver;
import com.example.mapleMyItemOption.web.argumentResolver.MyItemEquipmentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public ApiService apiService(){
        //return new ExternalApiService();
        return new InternalApiService();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CharacterResolver());
        resolvers.add(new MyItemEquipmentResolver());
    }
}
