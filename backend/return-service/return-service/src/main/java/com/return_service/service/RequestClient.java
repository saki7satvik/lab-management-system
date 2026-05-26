package com.return_service.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.return_service.dto.external.RequestResponseDTO;


@FeignClient(name = "REQUEST-SERVICE")
public interface RequestClient {
	
	@GetMapping("/requests")
	List<RequestResponseDTO> getAllRequests();
	
	@GetMapping("/requests/my")
	List<RequestResponseDTO> getMyRequests();
	
	@GetMapping("requests/{requestId}")
	RequestResponseDTO getRequestById(@PathVariable String requestId);

}
