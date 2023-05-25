package com.gg.gatewayservice.config;

import com.gg.gatewayservice.jwt.Jwt;
import com.gg.gatewayservice.jwt.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final Jwt jwt;

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter () {
        return new JwtAuthenticationTokenFilter( jwt );
    }

}
