package com.inventory_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory_service.dto.request.BulkStockUpdateDTO;
import com.inventory_service.dto.request.CreateComponentDTO;
import com.inventory_service.dto.request.UpdateStockDTO;
import com.inventory_service.dto.response.ComponentResponseDTO;
import com.inventory_service.dto.response.CurrentUser;
import com.inventory_service.dto.response.StockUpdateResponseDTO;
import com.inventory_service.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/components")
@Tag(name = "Inventory Controller", description = "APIs for managing lab components and stock")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // 🔥 CREATE COMPONENT
    @Operation(summary = "Create a new component (Instructor only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Component created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "409", description = "Component already exists")
    })
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<ComponentResponseDTO> createComponent(
            @Valid @RequestBody CreateComponentDTO dto,
            Authentication authentication) {
    	CurrentUser currentUser =
    		    (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                inventoryService.createComponent(dto, currentUser)
        );
    }

    // 🔥 INCREASE STOCK
    @Operation(summary = "Increase stock of a component (Instructor only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock increased successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid quantity"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/increase")
    public ResponseEntity<ComponentResponseDTO> increaseStock(
            @Valid @RequestBody UpdateStockDTO dto,
            Authentication authentication) {
    	
    	CurrentUser currentUser =
    		    (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                inventoryService.increaseStock(dto, currentUser)
        );
    }

    // 🔥 REDUCE STOCK
    @Operation(summary = "Reduce stock of a component (Instructor only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock reduced successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid quantity"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Component not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/reduce")
    public ResponseEntity<ComponentResponseDTO> reduceStock(
            @Valid @RequestBody UpdateStockDTO dto,
            Authentication authentication) {
    	
    	CurrentUser currentUser =
    		    (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                inventoryService.reduceStock(dto, currentUser)
        );
    }

    // 🔥 GET ALL COMPONENTS
    @Operation(summary = "Get all active components")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Components fetched successfully")
    })
    @GetMapping
    public ResponseEntity<List<ComponentResponseDTO>> getAllComponents() {
        return ResponseEntity.ok(
                inventoryService.getAllComponents()
        );
    }

    // 🔥 GET BY ID
    @Operation(summary = "Get component by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Component found"),
        @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ComponentResponseDTO> getComponentById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                inventoryService.getComponentById(id)
        );
    }

    // 🔥 VALIDATE (INTERNAL)
    @Operation(summary = "Validate component existence (internal API)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Validation result returned")
    })
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateComponents(
            @Valid @RequestBody List<String> componentIds) {

        return ResponseEntity.ok(
                inventoryService.validateComponents(componentIds)
        );
    }

    // 🔥 AVAILABILITY (INTERNAL)
    @Operation(summary = "Get available quantities of components (internal API)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Availability fetched successfully")
    })
    @PostMapping("/availability")
    public ResponseEntity<Map<String, Integer>> getAvailableQuantities(
            @Valid @RequestBody List<String> componentIds) {

        return ResponseEntity.ok(
                inventoryService.getAvailableQuantities(componentIds)
        );
    }

    // 🔥 DEDUCT STOCK
    @Operation(summary = "Deduct stock for multiple components (Instructor only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock deducted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Component not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/deduct")
    public ResponseEntity<List<StockUpdateResponseDTO>> deductStock(
            @Valid @RequestBody BulkStockUpdateDTO request,
            Authentication authentication) {
    	
    	CurrentUser currentUser =
    		    (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                inventoryService.deductStock(request, currentUser)
        );
    }

    // 🔥 ADD BACK STOCK
    @Operation(summary = "Add back stock (returns handling)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock added back successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/add-back")
    public ResponseEntity<List<StockUpdateResponseDTO>> addBackStock(
            @Valid @RequestBody BulkStockUpdateDTO request,
            Authentication authentication) {
    	
    	CurrentUser currentUser =
    		    (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                inventoryService.addBackStock(request, currentUser)
        );
    }
}