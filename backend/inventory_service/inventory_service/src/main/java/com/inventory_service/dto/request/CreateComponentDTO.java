package com.inventory_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateComponentDTO {
	@NotBlank(message = "Component name is required")
    private String name;

    private String description;

    @Min(value = 1, message = "Total quantity must be at least 1")
    private int totalQuantity;
    
    private String comments;
}
