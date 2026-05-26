package com.request_service.service;

import java.util.List;

import com.request_service.dto.request.ApproveRequestDTO;
import com.request_service.dto.request.CreateRequestDTO;
import com.request_service.dto.response.CurrentUser;
import com.request_service.dto.response.RequestResponseDTO;


public interface RequestService {
	RequestResponseDTO createRequest(CreateRequestDTO dto, CurrentUser currentUser);
	
	RequestResponseDTO getRequestById(String requestId, CurrentUser currentUser);
	
	List<RequestResponseDTO> getRequestsByUser(String targetUserId, CurrentUser currentUser);
	
	List<RequestResponseDTO> getAllRequests(CurrentUser currentUser);

	RequestResponseDTO rejectRequest(String requestId, CurrentUser currentUser);

	RequestResponseDTO approveRequest(String requestId, ApproveRequestDTO dto, CurrentUser currentUser);
}
