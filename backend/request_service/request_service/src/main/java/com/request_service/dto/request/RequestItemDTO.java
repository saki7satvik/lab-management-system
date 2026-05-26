package com.request_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestItemDTO {

    @NotNull(message = "Component ID is required")
    private String componentId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
