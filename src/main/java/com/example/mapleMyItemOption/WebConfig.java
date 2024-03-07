package com.example.mapleMyItemOption;

import com.example.mapleMyItemOption.api.ApiService;
import com.example.mapleMyItemOption.api.ExternalApiService;
import com.example.mapleMyItemOption.api.InternalApiService;
import com.example.mapleMyItemOption.domain.item.ItemAnalyzer;
import com.example.mapleMyItemOption.domain.item.PotentialOption;
import com.example.mapleMyItemOption.domain.repository.MemorySearchHistoryRepository;
import com.example.mapleMyItemOption.domain.repository.SearchHistoryRepository;
import com.example.mapleMyItemOption.web.argumentResolver.CharacterResolver;
import com.example.mapleMyItemOption.web.argumentResolver.MyItemEquipmentResolver;
import com.example.mapleMyItemOption.web.exceptionhandler.ExceptionHandler;
import com.example.mapleMyItemOption.web.interceptor.LogInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
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

    @PostConstruct
    public void initShortCategoryOption(){
        ItemAnalyzer.shortOptionCategory.put(PotentialOption.PER_LEVEL, "렙당"); // 캐릭터 기준 9레벨 당 : 렙당
        ItemAnalyzer.shortOptionCategory.put(PotentialOption.BOSS_DAMAGE, "보공%"); // 보스 몬스터 공격 시 데미지 : 보공%
        ItemAnalyzer.shortOptionCategory.put(PotentialOption.IGNORE_ARMOR, "방무%"); // 몬스터 방어율 무시 : 방무%
        ItemAnalyzer.shortOptionCategory.put(PotentialOption.CRITICAL_DAMAGE, "크뎀%"); // 크리티컬 데미지 : 크뎀%
        ItemAnalyzer.shortOptionCategory.put(PotentialOption.ITEM_DROP, "드랍%"); // 아이템 드롭률 : 드랍%
        ItemAnalyzer.shortOptionCategory.put(PotentialOption.MONEY_DROP, "메획%"); // 메소 획득량 : 메획%
        ItemAnalyzer.shortOptionCategory.put(PotentialOption.SKILL_COOL_TIME, "쿨감"); // 모든 스킬의 재사용 대기시간 : 쿨감
    }

    @Bean
    public SearchHistoryRepository searchHistoryRepository(){
        return new MemorySearchHistoryRepository();
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new ExceptionHandler());
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
