package com.user_service;

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

import com.user_service.dto.UserResponseDTO;
import com.user_service.entity.User;
import com.user_service.enums.Role;
import com.user_service.enums.Status;
import com.user_service.exception.ConflictException;
import com.user_service.exception.ResourceNotFoundException;
import com.user_service.exception.UnauthorizedException;
import com.user_service.repository.UserRepository;
import com.user_service.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceStatusTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // -----------------------------------------------------
    // 🧱 TEST DATA FACTORY
    // -----------------------------------------------------
    static class TestDataFactory {

        static User user(Role role, Status status, String collegeId) {
            User user = new User();
            user.setCollegeId(collegeId);
            user.setRole(role);
            user.setStatus(status);
            return user;
        }
    }

    // =====================================================
    // 🔴 DEACTIVATE USER TESTS
    // =====================================================
    @Nested
    class DeactivateUserTests {

        @Test
        @DisplayName("Should deactivate user successfully")
        void deactivate_success() {

            User current = TestDataFactory.user(Role.ADMIN, Status.ACTIVE, "CURR");
            User target = TestDataFactory.user(Role.STUDENT, Status.ACTIVE, "TGT");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));
            when(userRepository.findByCollegeId("TGT")).thenReturn(Optional.of(target));
            when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            UserResponseDTO response = userService.deactivateUser("TGT", "CURR");

            assertEquals(Status.INACTIVE, target.getStatus());
            verify(userRepository).save(target);
        }

        @Test
        @DisplayName("Should fail if current user not found")
        void deactivate_currentUserNotFound() {

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.deactivateUser("TGT", "CURR"));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail if current user inactive")
        void deactivate_currentUserInactive() {

            User current = TestDataFactory.user(Role.ADMIN, Status.INACTIVE, "CURR");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));

            assertThrows(UnauthorizedException.class,
                    () -> userService.deactivateUser("TGT", "CURR"));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail if target user not found")
        void deactivate_targetNotFound() {

            User current = TestDataFactory.user(Role.ADMIN, Status.ACTIVE, "CURR");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));
            when(userRepository.findByCollegeId("TGT")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.deactivateUser("TGT", "CURR"));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail if insufficient privileges")
        void deactivate_invalidRole() {

            User current = TestDataFactory.user(Role.INSTRUCTOR, Status.ACTIVE, "CURR");
            User target = TestDataFactory.user(Role.ADMIN, Status.ACTIVE, "TGT");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));
            when(userRepository.findByCollegeId("TGT")).thenReturn(Optional.of(target));

            assertThrows(UnauthorizedException.class,
                    () -> userService.deactivateUser("TGT", "CURR"));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail if already inactive")
        void deactivate_alreadyInactive() {

            User current = TestDataFactory.user(Role.ADMIN, Status.ACTIVE, "CURR");
            User target = TestDataFactory.user(Role.STUDENT, Status.INACTIVE, "TGT");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));
            when(userRepository.findByCollegeId("TGT")).thenReturn(Optional.of(target));

            assertThrows(ConflictException.class,
                    () -> userService.deactivateUser("TGT", "CURR"));

            verify(userRepository, never()).save(any());
        }
    }

    // =====================================================
    // 🟢 REACTIVATE USER TESTS
    // =====================================================
    @Nested
    class ReactivateUserTests {

        @Test
        @DisplayName("Should reactivate user successfully")
        void reactivate_success() {

            User current = TestDataFactory.user(Role.ADMIN, Status.ACTIVE, "CURR");
            User target = TestDataFactory.user(Role.STUDENT, Status.INACTIVE, "TGT");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));
            when(userRepository.findByCollegeId("TGT")).thenReturn(Optional.of(target));
            when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            UserResponseDTO response = userService.reactivateUser("TGT", "CURR");

            assertEquals(Status.ACTIVE, target.getStatus());
            verify(userRepository).save(target);
        }

        @Test
        @DisplayName("Should fail if already active")
        void reactivate_alreadyActive() {

            User current = TestDataFactory.user(Role.ADMIN, Status.ACTIVE, "CURR");
            User target = TestDataFactory.user(Role.STUDENT, Status.ACTIVE, "TGT");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));
            when(userRepository.findByCollegeId("TGT")).thenReturn(Optional.of(target));

            assertThrows(ConflictException.class,
                    () -> userService.reactivateUser("TGT", "CURR"));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail if insufficient privileges")
        void reactivate_invalidRole() {

            User current = TestDataFactory.user(Role.INSTRUCTOR, Status.ACTIVE, "CURR");
            User target = TestDataFactory.user(Role.ADMIN, Status.INACTIVE, "TGT");

            when(userRepository.findByCollegeId("CURR")).thenReturn(Optional.of(current));
            when(userRepository.findByCollegeId("TGT")).thenReturn(Optional.of(target));

            assertThrows(UnauthorizedException.class,
                    () -> userService.reactivateUser("TGT", "CURR"));

            verify(userRepository, never()).save(any());
        }
    }
}