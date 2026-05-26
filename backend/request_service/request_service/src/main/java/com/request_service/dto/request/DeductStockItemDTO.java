package com.request_service.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductStockItemDTO {

 @NotBlank(message = "Component id is required")
 private String componentId;

 @Min(value = 1, message = "Quantity must be greater than 0")
 private int quantity;
}
