package com.user_service.service;


import java.util.List;

import com.user_service.dto.CreateUserDTO;
import com.user_service.dto.UserResponseDTO;

public interface UserService {
	UserResponseDTO createUser(CreateUserDTO dto, String currentUserId);
    List<UserResponseDTO> getAllUsers(String currentUserCollegeId);
    UserResponseDTO deactivateUser(String targetCollegeId, String currentUserCollegeId);
    UserResponseDTO reactivateUser(String targetCollegeId, String currentUserCollegeId);
	UserResponseDTO getUserByCollegeId(String targetCollegeId, String currentUserCollegeId);
}
