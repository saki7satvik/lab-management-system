package com.return_service.dto;

import com.return_service.enums.ItemCondition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessReturnItemDTO {

	@NotBlank(message = "Request item id is required")
	private String requestItemId;

	@Positive(message = "Quantity must be greater than 0")
	private int quantityReturned;

	@NotNull(message = "Condition is required")
	private ItemCondition condition;

    private String damageRemarks;
}
