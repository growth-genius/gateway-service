package sgyj.gatewayservice.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply( Config config ) {
        return (( exchange, chain ) -> {
            var request = exchange.getRequest();
            var response = exchange.getResponse();
            log.info( "Global filter baseMessage: {}", config.getBaseMessage() );
            if(config.isPostLogger()) log.info( "Global Filter Start: request id -> {}", request.getId() );
            return chain.filter( exchange ).then( Mono.fromRunnable(() -> {
                if(config.isPostLogger()) log.info( "Global Filter End: response code -> {}", response.getStatusCode() );
            }));
        });
    }

    @Getter @Setter @ToString
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
