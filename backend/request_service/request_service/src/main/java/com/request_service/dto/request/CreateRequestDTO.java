package com.request_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

import com.request_service.enums.TargetType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRequestDTO {

    @NotNull(message = "Target type is required")
    private TargetType targetType;

    @NotNull(message = "Target ID is required")
    private String targetId;

    @NotEmpty(message = "At least one item is required")
    private List<RequestItemDTO> items;
}
