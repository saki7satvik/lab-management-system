package com.request_service.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkDeductStockRequestDTO {

 @NotEmpty(message = "Deduct items cannot be empty")
 @Valid
 private List<DeductStockItemDTO> items;
}
