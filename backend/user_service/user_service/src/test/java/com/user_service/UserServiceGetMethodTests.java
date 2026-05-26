package com.user_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.user_service.dto.CreateUserDTO;
import com.user_service.dto.UserResponseDTO;
import com.user_service.entity.User;
import com.user_service.enums.Role;
import com.user_service.enums.Status;
import com.user_service.exception.ResourceNotFoundException;
import com.user_service.exception.UnauthorizedException;
import com.user_service.repository.UserRepository;
import com.user_service.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceGetMethodTests {
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	// ========================================================
		// TEST DATA FACTORY
		// ========================================================
		static class TestDataFactory{
			
			static CreateUserDTO createUserDTO(Role role) {
				return new CreateUserDTO(
						"Test User",
						"SampleID_001",
						"test" + System.nanoTime() + "@mail.com",
						"9090909090",
						role
						);
			}
			
			static User creator(Role role, Status status) {
				User user = new User();
				user.setCollegeId("Creator_" + role);
				user.setRole(role);
				user.setStatus(status);
				return user;
			}
		}
		
		@Nested
		@DisplayName("Get All Users Tests")
		class GetAllUsersTests {

		    @Test
		    @DisplayName("Should return only accessible ACTIVE users")
		    void getAllUsers_success() {

		        User admin =
		                TestDataFactory.creator(Role.ADMIN, Status.ACTIVE);

		        User student =
		                TestDataFactory.creator(Role.STUDENT, Status.ACTIVE);

		        User instructor =
		                TestDataFactory.creator(Role.INSTRUCTOR, Status.ACTIVE);

		        User superAdmin =
		                TestDataFactory.creator(Role.SUPER_ADMIN, Status.ACTIVE);

		        User inactiveStudent =
		                TestDataFactory.creator(Role.STUDENT, Status.INACTIVE);

		        when(userRepository.findByCollegeId(admin.getCollegeId()))
		                .thenReturn(Optional.of(admin));

		        when(userRepository.findAll())
		                .thenReturn(List.of(
		                        admin,
		                        student,
		                        instructor,
		                        superAdmin,
		                        inactiveStudent
		                ));

		        List<UserResponseDTO> response =
		                userService.getAllUsers(admin.getCollegeId());

		        assertEquals(3, response.size());

		        assertTrue(
		                response.stream()
		                        .noneMatch(u -> u.getRole() == Role.SUPER_ADMIN)
		        );

		        assertTrue(
		                response.stream()
		                        .noneMatch(u -> u.getStatus() == Status.INACTIVE)
		        );
		    }

		    @Test
		    @DisplayName("Should fail if current user not found")
		    void getAllUsers_currentUserNotFound() {

		        when(userRepository.findByCollegeId("ADMIN_001"))
		                .thenReturn(Optional.empty());

		        assertThrows(
		                ResourceNotFoundException.class,
		                () -> userService.getAllUsers("ADMIN_001")
		        );

		        verify(userRepository, never())
		                .findAll();
		    }

		    @Test
		    @DisplayName("Should fail if current user inactive")
		    void getAllUsers_currentUserInactive() {

		        User inactiveAdmin =
		                TestDataFactory.creator(Role.ADMIN, Status.INACTIVE);

		        when(userRepository.findByCollegeId(inactiveAdmin.getCollegeId()))
		                .thenReturn(Optional.of(inactiveAdmin));

		        assertThrows(
		                UnauthorizedException.class,
		                () -> userService.getAllUsers(
		                        inactiveAdmin.getCollegeId()
		                )
		        );

		        verify(userRepository, never())
		                .findAll();
		    }

		    @ParameterizedTest
		    @EnumSource(Role.class)
		    @DisplayName("Should enforce hierarchy filtering")
		    void getAllUsers_roleFiltering(Role role) {

		        User current =
		                TestDataFactory.creator(role, Status.ACTIVE);

		        User superAdmin =
		                TestDataFactory.creator(Role.SUPER_ADMIN, Status.ACTIVE);

		        User admin =
		                TestDataFactory.creator(Role.ADMIN, Status.ACTIVE);

		        User instructor =
		                TestDataFactory.creator(Role.INSTRUCTOR, Status.ACTIVE);

		        User student =
		                TestDataFactory.creator(Role.STUDENT, Status.ACTIVE);

		        when(userRepository.findByCollegeId(current.getCollegeId()))
		                .thenReturn(Optional.of(current));

		        when(userRepository.findAll())
		                .thenReturn(List.of(
		                        superAdmin,
		                        admin,
		                        instructor,
		                        student
		                ));

		        List<UserResponseDTO> response =
		                userService.getAllUsers(current.getCollegeId());

		        assertTrue(
		                response.stream().allMatch(
		                        u -> current.getRole().ordinal()
		                                <= u.getRole().ordinal()
		                )
		        );
		    }
		}
		
		@Nested
		@DisplayName("Get User By CollegeId Tests")
		class GetUserByCollegeIdTests {

		    @Test
		    @DisplayName("Should return target user when access allowed")
		    void getUserByCollegeId_success() {

		        User admin =
		                TestDataFactory.creator(Role.ADMIN, Status.ACTIVE);

		        User student =
		                TestDataFactory.creator(Role.STUDENT, Status.ACTIVE);

		        when(userRepository.findByCollegeId(admin.getCollegeId()))
		                .thenReturn(Optional.of(admin));

		        when(userRepository.findByCollegeId(student.getCollegeId()))
		                .thenReturn(Optional.of(student));

		        UserResponseDTO response =
		                userService.getUserByCollegeId(
		                        student.getCollegeId(),
		                        admin.getCollegeId()
		                );

		        assertNotNull(response);

		        assertEquals(
		                student.getCollegeId(),
		                response.getCollegeId()
		        );

		        assertEquals(
		                Role.STUDENT,
		                response.getRole()
		        );
		    }

		    @Test
		    @DisplayName("Should fail if current user not found")
		    void getUserByCollegeId_currentUserNotFound() {

		        when(userRepository.findByCollegeId("ADMIN_001"))
		                .thenReturn(Optional.empty());

		        assertThrows(
		                ResourceNotFoundException.class,
		                () -> userService.getUserByCollegeId(
		                        "STU_001",
		                        "ADMIN_001"
		                )
		        );
		    }

		    @Test
		    @DisplayName("Should fail if current user inactive")
		    void getUserByCollegeId_currentUserInactive() {

		        User inactiveAdmin =
		                TestDataFactory.creator(Role.ADMIN, Status.INACTIVE);

		        when(userRepository.findByCollegeId(inactiveAdmin.getCollegeId()))
		                .thenReturn(Optional.of(inactiveAdmin));

		        assertThrows(
		                UnauthorizedException.class,
		                () -> userService.getUserByCollegeId(
		                        "STU_001",
		                        inactiveAdmin.getCollegeId()
		                )
		        );
		    }

		    @Test
		    @DisplayName("Should fail if target user not found")
		    void getUserByCollegeId_targetUserNotFound() {

		        User admin =
		                TestDataFactory.creator(Role.ADMIN, Status.ACTIVE);

		        when(userRepository.findByCollegeId(admin.getCollegeId()))
		                .thenReturn(Optional.of(admin));

		        when(userRepository.findByCollegeId("STU_001"))
		                .thenReturn(Optional.empty());

		        assertThrows(
		                ResourceNotFoundException.class,
		                () -> userService.getUserByCollegeId(
		                        "STU_001",
		                        admin.getCollegeId()
		                )
		        );
		    }

		    @Test
		    @DisplayName("Should fail if target user inactive")
		    void getUserByCollegeId_targetUserInactive() {

		        User admin =
		                TestDataFactory.creator(Role.ADMIN, Status.ACTIVE);

		        User inactiveStudent =
		                TestDataFactory.creator(Role.STUDENT, Status.INACTIVE);

		        when(userRepository.findByCollegeId(admin.getCollegeId()))
		                .thenReturn(Optional.of(admin));

		        when(userRepository.findByCollegeId(inactiveStudent.getCollegeId()))
		                .thenReturn(Optional.of(inactiveStudent));

		        assertThrows(
		                ResourceNotFoundException.class,
		                () -> userService.getUserByCollegeId(
		                        inactiveStudent.getCollegeId(),
		                        admin.getCollegeId()
		                )
		        );
		    }

		    @Test
		    @DisplayName("Should deny access to higher role")
		    void getUserByCollegeId_accessDenied() {

		        User student =
		                TestDataFactory.creator(Role.STUDENT, Status.ACTIVE);

		        User admin =
		                TestDataFactory.creator(Role.ADMIN, Status.ACTIVE);

		        when(userRepository.findByCollegeId(student.getCollegeId()))
		                .thenReturn(Optional.of(student));

		        when(userRepository.findByCollegeId(admin.getCollegeId()))
		                .thenReturn(Optional.of(admin));

		        assertThrows(
		                UnauthorizedException.class,
		                () -> userService.getUserByCollegeId(
		                        admin.getCollegeId(),
		                        student.getCollegeId()
		                )
		        );
		    }
		}
}
