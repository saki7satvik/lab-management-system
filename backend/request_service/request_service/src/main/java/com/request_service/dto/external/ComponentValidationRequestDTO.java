package com.request_service.dto.external;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentValidationRequestDTO {

    private String componentId;
    private int quantity;
}