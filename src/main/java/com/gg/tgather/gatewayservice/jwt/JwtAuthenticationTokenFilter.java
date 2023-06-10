package com.gg.tgather.gatewayservice.jwt;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtAuthenticationTokenFilter extends AbstractGatewayFilterFactory<JwtAuthenticationTokenFilter.Config> {

    private final Jwt jwt;
    private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

    public JwtAuthenticationTokenFilter(Jwt jwt) {
        super(Config.class);
        this.jwt = jwt;
    }

    @Override
    public GatewayFilter apply(Config config ){
        return (( exchange, chain ) -> {
            ServerHttpRequest request = exchange.getRequest();

            if( !request.getHeaders().containsKey( HttpHeaders.AUTHORIZATION )) return onError(exchange, HttpStatus.UNAUTHORIZED, "no authorization header");

            List<String> headers = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

            if( headers == null || headers.isEmpty() ) return onError(exchange, HttpStatus.UNAUTHORIZED, "Authentication is not present");

            Optional<String> optionalToken = obtainAuthorizationToken(headers.get( 0 ));
            if(optionalToken.isEmpty()) return onError(exchange, HttpStatus.UNAUTHORIZED, "JWT token is not valid");
            jwt.verify( optionalToken.get() );
            return chain.filter( exchange );
        });
    }

    private Mono<Void> onError(ServerWebExchange serverWebExchange, HttpStatus httpStatus, String errorMessage ) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }


    private Optional<String> obtainAuthorizationToken(String token) {
        if (token != null) {
            if (log.isDebugEnabled())
                log.error("Jwt authorization api detected: {}", token);
            token = URLDecoder.decode(token, StandardCharsets.UTF_8 );
            String[] parts = token.split(" ");
            if (parts.length == 2) {
                String scheme = parts[0];
                String credentials = parts[1];
                return BEARER.matcher(scheme).matches() ? Optional.of(credentials) : Optional.empty();
            }
        }

        return Optional.empty();
    }

    public static class Config {

    }
}