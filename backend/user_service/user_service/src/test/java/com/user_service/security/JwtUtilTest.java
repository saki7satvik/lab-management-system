package com.user_service.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_success() {

        String token = jwtUtil.generateToken(
                "ADMIN_001",
                "ADMIN",
                "ACTIVE"
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractCollegeId_success() {

        String token = jwtUtil.generateToken(
                "ADMIN_001",
                "ADMIN",
                "ACTIVE"
        );

        String collegeId =
                jwtUtil.extractCollegeId(token);

        assertEquals("ADMIN_001", collegeId);
    }

    @Test
    void extractRole_success() {

        String token = jwtUtil.generateToken(
                "ADMIN_001",
                "ADMIN",
                "ACTIVE"
        );

        String role =
                jwtUtil.extractRole(token);

        assertEquals("ADMIN", role);
    }

    @Test
    void extractStatus_success() {

        String token = jwtUtil.generateToken(
                "ADMIN_001",
                "ADMIN",
                "ACTIVE"
        );

        String status =
                jwtUtil.extractStatus(token);

        assertEquals("ACTIVE", status);
    }

    @Test
    void isTokenValid_validToken() {

        String token = jwtUtil.generateToken(
                "ADMIN_001",
                "ADMIN",
                "ACTIVE"
        );

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_invalidToken() {

        String invalid =
                "invalid.jwt.token";

        assertFalse(jwtUtil.isTokenValid(invalid));
    }

    @Test
    void extractClaims_invalidToken() {

        assertThrows(
                Exception.class,
                () -> jwtUtil.extractClaims(
                        "invalid.jwt.token"
                )
        );
    }
}