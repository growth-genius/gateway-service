package com.gg.tgather.gatewayservice.handler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

@Slf4j
@Order(-1)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException responsestatusexception) {
            response.setStatusCode(responsestatusexception.getStatusCode());
        } else if( ex instanceof TokenExpiredException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
        }


        Map<String, String> errorMap = new HashMap<>();
        StringTokenizer stringTokenizer = new StringTokenizer(Objects.requireNonNull(response.getStatusCode()).toString(), " ");
        if(stringTokenizer.hasMoreTokens()) {
            errorMap.put("success", "false");
            errorMap.put("status", stringTokenizer.nextToken());
            errorMap.put("response", "{}");
            errorMap.put("message", ex.getMessage());
        }

        String error = "Gateway Error";

        try {
            error = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorMap);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException : " + e.getMessage());
        }

        byte[] bytes = error.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }


}