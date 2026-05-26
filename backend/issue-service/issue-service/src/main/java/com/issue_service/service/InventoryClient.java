package com.issue_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.issue_service.dto.response.BulkDeductRequestDTO;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/components/deduct")
    void deductStock(@RequestBody BulkDeductRequestDTO request);
}
