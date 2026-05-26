package com.request_service.dto.request;

import com.request_service.dto.response.RequestItemResponseDTO;
import com.request_service.enums.RequestStatus;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestResponseDTO {

 private String id;

 private String createdBy;

 private RequestStatus status;

 private LocalDateTime createdAt;

 private List<RequestItemResponseDTO> items;
}
