package com.jtframework.base.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Slf4j
public class CorsConfig {

    @Value("${spring.cors:true}")
    private boolean enableCors;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        log.info(" 设置 跨域状态 ，spring.cors 状态 是 :{}", enableCors);
//        if (enableCors) {
//            registry.addMapping("/**")
//                    .allowedOrigins("*")
//                    .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
//                    .allowCredentials(true)
//                    .maxAge(3600)
//                    .allowedHeaders("*");
//        }
//    }
    @Bean
    public FilterRegistrationBean corsFilter() {
        log.info(" 设置 跨域状态 ，spring.cors 状态 是 :{}", enableCors);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置你要允许的网站域名，如果全允许则设为 *
        config.addAllowedOrigin("*");
        // 如果要限制 HEADER 或 METHOD 请自行更改
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        // 这个顺序很重要哦，为避免麻烦请设置在最前
        bean.setOrder(0);
        return bean;
    }
}