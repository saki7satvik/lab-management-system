package com.inventory_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class StockUpdateItemDTO {

    @NotNull
    private String componentId;

    @Min(1)
    private int quantity;
}