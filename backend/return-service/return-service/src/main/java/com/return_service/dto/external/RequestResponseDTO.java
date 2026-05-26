package com.return_service.dto.external;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.return_service.enums.RequestStatus;
import com.return_service.enums.TargetType;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestResponseDTO {

    private String id;

    private String createdBy;

    private TargetType targetType;

    private String targetId;

    private RequestStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<RequestItemResponseDTO> items;
}
