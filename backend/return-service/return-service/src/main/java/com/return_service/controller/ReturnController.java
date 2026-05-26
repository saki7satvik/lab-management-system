package com.return_service.controller;

import com.return_service.dto.*;
import com.return_service.service.ReturnService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
@Tag(
    name = "Return Controller",
    description = "APIs for processing and tracking returned components"
)
public class ReturnController {

    private final ReturnService returnService;

    // 🔥 1. PROCESS RETURN
    @Operation(summary = "Process return for approved request")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Return processed successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid return request"
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Access denied"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Request or component not found"
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Invalid return quantity"
        )
    })
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<ReturnResponseDTO> processReturn(
            @Valid @RequestBody ProcessReturnRequestDTO request,
            Authentication authentication
    ) {

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                returnService.processReturn(
                        request,
                        currentUser
                )
        );
    }

    // 🔥 2. GET RETURN BY ID
    @Operation(summary = "Get return details by ID")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Return found"
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Access denied"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Return not found"
        )
    })
    @PreAuthorize(
            "hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ReturnResponseDTO> getReturnById(
            @PathVariable String id,
            Authentication authentication
    ) {

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                returnService.getReturnById(
                        id,
                        currentUser
                )
        );
    }

    // 🔥 3. GET RETURNS
    @Operation(summary = "Get returns (role filtered)")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Returns fetched successfully"
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Access denied"
        )
    })
    @PreAuthorize(
            "hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')"
    )
    @GetMapping
    public ResponseEntity<List<ReturnResponseDTO>> getReturns(
            Authentication authentication
    ) {

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                returnService.getReturns(currentUser)
        );
    }

    // 🔥 4. GET RETURNS BY REQUEST
    @Operation(summary = "Get returns associated with a request")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Returns fetched successfully"
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Access denied"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Request not found"
        )
    })
    @PreAuthorize(
            "hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')"
    )
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<ReturnResponseDTO>> getReturnsByRequest(
            @PathVariable String requestId,
            Authentication authentication
    ) {

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                returnService.getReturnsByRequest(
                        requestId,
                        currentUser
                )
        );
    }
}