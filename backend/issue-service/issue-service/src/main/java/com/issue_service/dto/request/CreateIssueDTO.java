package com.issue_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class CreateIssueDTO {

    @NotNull
    private String requestId;

    @NotEmpty
    private List<IssueItemDTO> items;

    // getters & setters
}
