package com.return_service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InspectReturnRequestDTO {

    @NotEmpty
    private List<InspectReturnItemDTO> items;

    private String remarks;
}