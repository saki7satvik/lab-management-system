package com.return_service.dto;

import com.return_service.enums.ItemCondition;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InspectReturnItemDTO {

    @NotNull
    private String returnItemId;

    @NotNull
    private ItemCondition condition;

    private String damageRemarks;
}