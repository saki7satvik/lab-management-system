package com.user_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.user_service.entity.User;
import com.user_service.enums.Role;
import com.user_service.enums.Status;
import com.user_service.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final String SUPER_ADMIN_COLLEGE_ID = "SUPER_ADMIN_001";
    private static final String DEFAULT_PASSWORD = "welcome@123";

    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        return args -> {

            // 🔍 Check if SUPER_ADMIN already exists
            boolean exists = userRepository.existsByCollegeId(SUPER_ADMIN_COLLEGE_ID);

            if (!exists) {

                User superAdmin = new User();
                superAdmin.setName("Super Admin");
                superAdmin.setCollegeId(SUPER_ADMIN_COLLEGE_ID);
                superAdmin.setEmail("superadmin@lab.com");
                superAdmin.setPhone("0000000000");
                superAdmin.setRole(Role.SUPER_ADMIN);
                superAdmin.setStatus(Status.ACTIVE);

                // 🔐 Hash password
                superAdmin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));

                userRepository.save(superAdmin);

                System.out.println("🔥 SUPER_ADMIN created successfully");
            } else {
                System.out.println("✅ SUPER_ADMIN already exists");
            }
        };
    }
}