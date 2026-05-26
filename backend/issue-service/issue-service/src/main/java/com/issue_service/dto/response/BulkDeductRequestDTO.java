package com.issue_service.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BulkDeductRequestDTO {

    private List<DeductRequestDTO> items;

    // getters & setters
}
