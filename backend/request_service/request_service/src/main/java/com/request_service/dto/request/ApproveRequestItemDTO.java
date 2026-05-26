package com.request_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRequestItemDTO {

    @NotNull(message = "RequestItem ID is required")
    private String requestItemId;

    @Min(value = 0, message = "Approved quantity cannot be negative")
    private int quantityApproved;
}
