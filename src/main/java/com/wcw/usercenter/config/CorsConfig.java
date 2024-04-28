package com.wcw.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    private CorsConfiguration corsConfig(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*"); //允许所有请求头
        corsConfiguration.addAllowedMethod("*"); //允许所有请求方法
        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.addAllowedOrigin("http://112.124.30.198:5173/");
//        corsConfiguration.addAllowedOrigin("http://112.124.30.198:80/");
//        corsConfiguration.addAllowedOrigin("http://localhost:5173/");
//        corsConfiguration.addAllowedOrigin("http://127.0.0.1:5173/");
        //corsConfiguration.addAllowedOrigin("*");//允许所有的请求类型
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }
    @Bean
    public CorsFilter corsFilter(){
        //存储request与跨域配置信息的容器，基于url的映射
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfig());
        return new CorsFilter(source);
    }

}
