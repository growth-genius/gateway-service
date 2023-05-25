package gg.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import gg.gatewayservice.jwt.Jwt;
import gg.gatewayservice.jwt.JwtAuthenticationTokenFilter;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final Jwt jwt;

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter () {
        return new JwtAuthenticationTokenFilter( jwt );
    }

}
