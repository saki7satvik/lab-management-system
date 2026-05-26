package com.issue_service.dto.response;

import com.issue_service.enums.Role;
import com.issue_service.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CurrentUser {
	private String id;
	private Role role;
	private Status status;
}