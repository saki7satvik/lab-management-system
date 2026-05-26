package com.request_service.dto.response;

import com.request_service.enums.RequestItemStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestItemResponseDTO {

    private String id;

    private String componentId;

    private String componentName; // fetched from Inventory Service

    private int quantityRequested;

    private int quantityApproved;

    private RequestItemStatus status;
}