package com.issue_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IssueItemResponseDTO {

    private String issueItemId;
    private String componentId;
    private int quantityIssued;

    // getters & setters
}
