package com.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.entity.User;
import com.user_service.enums.Role;

public interface UserRepository extends JpaRepository<User, String>{
	boolean existsByRole(Role role);
	
	boolean existsByCollegeId(String collegeId);
	
	boolean existsByEmail(String email);
	
	Optional<User> findByCollegeId(String collegeId); 
}
