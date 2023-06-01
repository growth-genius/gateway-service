package com.gg.tgather.gatewayservice.config;

import com.gg.tgather.gatewayservice.jwt.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfigure {

    @Bean
    public Jwt jwt ( JwtTokenConfigure jwtTokenConfigure ) {
        return new Jwt( jwtTokenConfigure.getIssuer(), jwtTokenConfigure.getClientSecret() );
    }

}