package com.inventory_service.security;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.databind.ObjectMapper;

class CustomAccessDeniedHandlerTest {

	private final CustomAccessDeniedHandler handler =
	        new CustomAccessDeniedHandler(
	                new ObjectMapper().findAndRegisterModules()
	        );

    @Test
    void handle_shouldReturn403()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/users");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        handler.handle(
                request,
                response,
                new AccessDeniedException("Access denied")
        );

        assertEquals(403, response.getStatus());

        String body =
                response.getContentAsString();

        assertTrue(body.contains("Forbidden"));
        assertTrue(body.contains("Access denied"));
    }
}
