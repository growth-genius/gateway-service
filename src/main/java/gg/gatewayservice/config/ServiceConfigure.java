package gg.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import gg.gatewayservice.jwt.Jwt;

@Configuration
public class ServiceConfigure {

    @Bean
    public Jwt jwt ( JwtTokenConfigure jwtTokenConfigure ) {
        return new Jwt( jwtTokenConfigure.getIssuer(), jwtTokenConfigure.getClientSecret() );
    }

}