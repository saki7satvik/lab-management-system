package com.issue_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeductRequestDTO {

    private String componentId;
    private int quantity;

    // constructor, getters
}
