package com.inventory_service.dto.response;

import lombok.Getter;

@Getter
public class StockUpdateResponseDTO {

    private String componentId;
    private int updatedQuantity;
    private int remainingQuantity;

    public StockUpdateResponseDTO(String componentId, int updatedQuantity, int remainingQuantity) {
        this.componentId = componentId;
        this.updatedQuantity = updatedQuantity;
        this.remainingQuantity = remainingQuantity;
    }
}
