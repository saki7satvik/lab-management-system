package com.request_service.dto.external;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentValidationResponseDTO {

    private String componentId;

    private boolean exists;

    private int availableQuantity;

    private int requestedQuantity;

    private boolean fullyAvailable;

    private boolean partiallyAvailable;
}
