package com.issue_service.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.issue_service.dto.response.RequestResponseDTO;

@FeignClient(name = "request-service")
public interface RequestClient {

    @GetMapping("/requests/{id}")
    RequestResponseDTO getRequestById(@PathVariable("id") String requestId);

    @PutMapping("/requests/{id}/mark-issued")
    void markAsIssued(@PathVariable("id") String requestId);
    
//    @GetMapping("/requests/user")
//    List<RequestResponseDTO> getCurrentUserRequests();
}
