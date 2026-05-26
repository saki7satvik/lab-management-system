package com.user_service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.user_service.entity.User;
import com.user_service.enums.Role;
import com.user_service.enums.Status;
import com.user_service.exception.UnauthorizedException;
import com.user_service.repository.UserRepository;
import com.user_service.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    // =====================================================
    // TEST DATA FACTORY
    // =====================================================

    static class TestDataFactory {

        static LoginRequestDTO loginRequest(
                String collegeId,
                String password
        ) {

            LoginRequestDTO dto =
                    new LoginRequestDTO();

            try {

                var collegeIdField =
                        LoginRequestDTO.class
                                .getDeclaredField("collegeId");

                collegeIdField.setAccessible(true);

                collegeIdField.set(dto, collegeId);

                var passwordField =
                        LoginRequestDTO.class
                                .getDeclaredField("password");

                passwordField.setAccessible(true);

                passwordField.set(dto, password);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return dto;
        }

        static User user(
                Role role,
                Status status
        ) {

            User user = new User();

            user.setCollegeId("ADMIN_001");
            user.setPassword("encoded_password");

            user.setRole(role);
            user.setStatus(status);

            return user;
        }
    }

    // =====================================================
    // LOGIN TESTS
    // =====================================================

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void login_success() {

            LoginRequestDTO request =
                    TestDataFactory.loginRequest(
                            "ADMIN_001",
                            "welcome@123"
                    );

            User user =
                    TestDataFactory.user(
                            Role.ADMIN,
                            Status.ACTIVE
                    );

            when(userRepository.findByCollegeId("ADMIN_001"))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(
                    "welcome@123",
                    "encoded_password"
            )).thenReturn(true);

            when(jwtUtil.generateToken(
                    user.getCollegeId(),
                    user.getRole().name(),
                    user.getStatus().name()
            )).thenReturn("jwt-token");

            LoginResponseDTO response =
                    authController.login(request)
                            .getBody();

            assertNotNull(response);

            assertEquals(
                    "jwt-token",
                    response.getToken()
            );

            verify(jwtUtil).generateToken(
                    "ADMIN_001",
                    "ADMIN",
                    "ACTIVE"
            );
        }

        @Test
        @DisplayName("Should fail when user not found")
        void login_userNotFound() {

            LoginRequestDTO request =
                    TestDataFactory.loginRequest(
                            "ADMIN_001",
                            "welcome@123"
                    );

            when(userRepository.findByCollegeId("ADMIN_001"))
                    .thenReturn(Optional.empty());

            UnauthorizedException ex =
                    assertThrows(
                            UnauthorizedException.class,
                            () -> authController.login(request)
                    );

            assertEquals(
                    "Invalid credentials",
                    ex.getMessage()
            );

            verify(jwtUtil, never())
                    .generateToken(any(), any(), any());
        }

        @Test
        @DisplayName("Should fail when password incorrect")
        void login_invalidPassword() {

            LoginRequestDTO request =
                    TestDataFactory.loginRequest(
                            "ADMIN_001",
                            "wrong_password"
                    );

            User user =
                    TestDataFactory.user(
                            Role.ADMIN,
                            Status.ACTIVE
                    );

            when(userRepository.findByCollegeId("ADMIN_001"))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(
                    "wrong_password",
                    "encoded_password"
            )).thenReturn(false);

            UnauthorizedException ex =
                    assertThrows(
                            UnauthorizedException.class,
                            () -> authController.login(request)
                    );

            assertEquals(
                    "Invalid credentials",
                    ex.getMessage()
            );

            verify(jwtUtil, never())
                    .generateToken(any(), any(), any());
        }

        @Test
        @DisplayName("Should fail when user inactive")
        void login_inactiveUser() {

            LoginRequestDTO request =
                    TestDataFactory.loginRequest(
                            "ADMIN_001",
                            "welcome@123"
                    );

            User user =
                    TestDataFactory.user(
                            Role.ADMIN,
                            Status.INACTIVE
                    );

            when(userRepository.findByCollegeId("ADMIN_001"))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(
                    "welcome@123",
                    "encoded_password"
            )).thenReturn(true);

            UnauthorizedException ex =
                    assertThrows(
                            UnauthorizedException.class,
                            () -> authController.login(request)
                    );

            assertEquals(
                    "User is inactive",
                    ex.getMessage()
            );

            verify(jwtUtil, never())
                    .generateToken(any(), any(), any());
        }
    }
}