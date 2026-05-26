package com.return_service.service;

import com.return_service.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    @PostMapping("/components/add-back")
    List<AddBackResponseDTO> addBackStock(
            @RequestBody BulkAddBackRequestDTO request
    );
    
    
}
