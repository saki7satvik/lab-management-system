package com.return_service.dto.external;

import lombok.Data;

@Data
public class AddBackResponseDTO {
    private String componentId;
    private int quantityAdded;
    private int remainingQuantity;
}