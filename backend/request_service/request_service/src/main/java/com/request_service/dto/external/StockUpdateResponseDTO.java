package com.request_service.dto.external;

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
