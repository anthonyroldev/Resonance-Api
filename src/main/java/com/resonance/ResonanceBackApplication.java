package com.resonance;

import com.resonance.components.RateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ResonanceBackApplication {

    static void main(String[] args) {
        SpringApplication.run(ResonanceBackApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<RateLimiter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimiter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimiter());
        registration.addUrlPatterns("/api/*");
        return registration;
    }

}
