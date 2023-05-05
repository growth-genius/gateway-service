package sgyj.gatewayservice.jwt;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
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
            if( !request.getHeaders().containsKey( HttpHeaders.AUTHORIZATION )) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED );
            }

            String header = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get( 0 );
            Optional<String> optionalToken = obtainAuthorizationToken(header);
            if(optionalToken.isEmpty()) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter( exchange );
        });
    }

    private Mono<Void> onError( ServerWebExchange serverWebExchange, String errorMessage, HttpStatus httpStatus ) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.setStatusCode( httpStatus );
        log.error( errorMessage );
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

    private Jwt.Claims verify(String token) {
        return jwt.verify(token);
    }

    public static class Config {

    }
}