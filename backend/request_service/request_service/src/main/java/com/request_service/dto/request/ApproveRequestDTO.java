package com.request_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRequestDTO {

    @NotEmpty(message = "Approval items cannot be empty")
    private List<ApproveRequestItemDTO> items;
}
