package com.gg.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply( Config config ) {
        return (( exchange, chain ) -> {
            var request = exchange.getRequest();
            var response = exchange.getResponse();
            log.info( "Custom PRE filter : request id -> {}", request.getId() );

            return chain.filter( exchange ).then( Mono.fromRunnable(() -> {
                log.info( "Custom POST filter : response code -> {}", response.getStatusCode() );
            }));
        });
    }

    public static class Config{

    }

}
