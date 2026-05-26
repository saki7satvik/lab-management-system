package com.return_service.service;

import com.return_service.dto.*;

import java.util.List;

public interface ReturnService {

    ReturnResponseDTO processReturn(
            ProcessReturnRequestDTO request,
            CurrentUser currentUser
    );

    ReturnResponseDTO getReturnById(
            String returnId,
            CurrentUser currentUser
    );

    List<ReturnResponseDTO> getReturnsByRequest(
            String requestId,
            CurrentUser currentUser
    );

    List<ReturnResponseDTO> getReturns(
            CurrentUser currentUser
    );
}