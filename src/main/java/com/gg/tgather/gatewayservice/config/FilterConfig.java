package com.gg.tgather.gatewayservice.config;

import com.gg.tgather.gatewayservice.jwt.Jwt;
import com.gg.tgather.gatewayservice.jwt.JwtAuthenticationTokenFilter;
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
