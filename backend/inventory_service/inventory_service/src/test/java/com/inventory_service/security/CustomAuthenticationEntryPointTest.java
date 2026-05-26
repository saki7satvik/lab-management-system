package com.inventory_service.security;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;

import com.fasterxml.jackson.databind.ObjectMapper;


public class CustomAuthenticationEntryPointTest {
	private final CustomAuthenticationEntryPoint entryPoint =
	        new CustomAuthenticationEntryPoint(
	                new ObjectMapper().findAndRegisterModules()
	        );
	
	@Test
    void commence_shouldReturn401()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/users");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException("Invalid token")
        );

        assertEquals(401, response.getStatus());

        String body =
                response.getContentAsString();

        assertTrue(body.contains("Unauthorized"));
        assertTrue(body.contains("Invalid token"));
    }
}
