package com.return_service.dto;

import com.return_service.enums.OverallCondition;
import com.return_service.enums.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReturnResponseDTO {

    private String id;
    private String requestId;

    private String returnedBy;
    private String inspectedBy;

    private ReturnStatus status;
    private OverallCondition overallCondition;

    private boolean fineGenerated;

    private String remarks;

    private LocalDateTime createdAt;
    private LocalDateTime inspectedAt;

    private List<ReturnItemResponseDTO> items;
}