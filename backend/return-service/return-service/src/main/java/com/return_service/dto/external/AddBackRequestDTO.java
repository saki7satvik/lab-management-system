package com.return_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddBackRequestDTO {
    private String componentId;
    private int quantity;
}