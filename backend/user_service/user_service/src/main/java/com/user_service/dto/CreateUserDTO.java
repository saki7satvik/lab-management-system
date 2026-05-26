package com.user_service.dto;

import com.user_service.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
	
	@NotBlank
    private String name;

    @NotBlank
    private String collegeId;

    @Email
    @NotBlank
    private String email;

    private String phone;

    @NotNull
    private Role role;
}
