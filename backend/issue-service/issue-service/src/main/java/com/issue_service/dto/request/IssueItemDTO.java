package com.issue_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class IssueItemDTO {

    @NotNull
    private String requestItemId;

    @Min(1)
    private int quantity;

    // getters & setters
}
