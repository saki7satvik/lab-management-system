package com.api_gateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> OPEN_API_ENDPOINTS = List.of(

            // Auth
            "/auth/login",
            "/auth/register",

            // Eureka
            "/eureka",

            // Swagger
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",

            // Aggregated Swagger
            "/user-service/v3/api-docs",
            "/inventory-service/v3/api-docs",
            "/request-service/v3/api-docs",
            "/issue-service/v3/api-docs",
            "/return-service/v3/api-docs"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_API_ENDPOINTS
                    .stream()
                    .noneMatch(uri ->
                            request.getURI()
                                   .getPath()
                                   .contains(uri));
}