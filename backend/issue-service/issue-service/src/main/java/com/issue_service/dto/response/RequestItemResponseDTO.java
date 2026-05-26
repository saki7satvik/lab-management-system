package com.issue_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RequestItemResponseDTO {
	@JsonProperty("id")
    private String requestItemId;
    private String componentId;
    private int quantityApproved;

    // getters & setters
}
