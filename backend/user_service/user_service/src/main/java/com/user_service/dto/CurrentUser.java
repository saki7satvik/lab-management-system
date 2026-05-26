package com.user_service.dto;

import com.user_service.enums.Role;
import com.user_service.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CurrentUser {
	private String id;
	private Role role;
	private Status status;
}

