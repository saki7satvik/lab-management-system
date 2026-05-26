package com.return_service.dto.external;


import com.return_service.enums.RequestItemStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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