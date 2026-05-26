package com.inventory_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentResponseDTO {
	private String id;
    private String name;
    private String description;

    private int totalQuantity;
    private int availableQuantity;
	
}
