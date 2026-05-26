package com.inventory_service.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class BulkStockUpdateDTO {

    @NotEmpty
    private List<StockUpdateItemDTO> items;

}
