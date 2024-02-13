package com.example.mapleMyItemOption;

import com.example.mapleMyItemOption.api.ApiService;
import com.example.mapleMyItemOption.api.ExternalApiService;
import com.example.mapleMyItemOption.api.InternalApiService;
import com.example.mapleMyItemOption.domain.repository.MemorySearchHistoryRepository;
import com.example.mapleMyItemOption.domain.repository.SearchHistoryRepository;
import com.example.mapleMyItemOption.web.argumentResolver.CharacterResolver;
import com.example.mapleMyItemOption.web.argumentResolver.MyItemEquipmentResolver;
import com.example.mapleMyItemOption.web.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ApiService apiService(){
        return new ExternalApiService();
        //return new InternalApiService();
    }

    @Bean
    public SearchHistoryRepository searchHistoryRepository(){
        return new MemorySearchHistoryRepository();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CharacterResolver());
        resolvers.add(new MyItemEquipmentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/my-character/**",  "/*.ico", "/error");

    }
}
