package com.inventory_service.dto.response;

import com.inventory_service.enums.Role;
import com.inventory_service.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CurrentUser {
	private String id;
	private Role role;
	private Status status;
}
