package com.example.mapleMyItemOption;

import com.example.mapleMyItemOption.api.ApiService;
import com.example.mapleMyItemOption.api.ExternalApiService;
import com.example.mapleMyItemOption.api.InternalApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public ApiService apiService(){
        //return new ExternalApiService();
        return new InternalApiService();
    }
}
