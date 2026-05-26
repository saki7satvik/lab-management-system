package com.issue_service.dto.response;

import java.util.List;

import com.issue_service.enums.RequestStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RequestResponseDTO {

    private String id;
    private String createdBy;
    private RequestStatus status;
    private List<RequestItemResponseDTO> items;

    // getters & setters
}
