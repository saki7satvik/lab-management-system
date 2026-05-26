package com.inventory_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .version("1.0")
                        .description("Inventory Service for managing components and stock"))

                // 🔐 Apply JWT globally
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                // 🔐 Define JWT scheme
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi inventoryApi() {
        return GroupedOpenApi.builder()
                .group("inventory-service")
                .pathsToMatch("/components/**")
                
                // 🔥 Remove JWT requirement for internal APIs if needed
                .addOperationCustomizer((operation, handlerMethod) -> {

                    String methodName = handlerMethod.getMethod().getName();

                    // internal APIs (optional to skip auth in Swagger UI)
                    if (methodName.equals("validateComponents") ||
                        methodName.equals("getAvailableQuantities")) {

                        operation.setSecurity(null);
                    }

                    return operation;
                })
                .build();
    }
}