package com.request_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.request_service.dto.response.CurrentUser;
import com.request_service.dto.response.RequestResponseDTO;
import com.request_service.entity.Request;
import com.request_service.entity.RequestItem;
import com.request_service.enums.RequestItemStatus;
import com.request_service.enums.RequestStatus;
import com.request_service.enums.Role;
import com.request_service.enums.Status;
import com.request_service.exception.ConflictException;
import com.request_service.exception.ResourceNotFoundException;
import com.request_service.exception.UnauthorizedException;
import com.request_service.repository.RequestRepository;
import com.request_service.service.impl.RequestServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RejectRequestTests {
	@Mock
    private RequestRepository requestRepository;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private RequestServiceImpl requestService;
    
    private CurrentUser instructorUser;
    private CurrentUser studentUser;
    private CurrentUser inactiveInstructor;

    private Request request;
    private RequestItem item1;
    private RequestItem item2;
    
    @BeforeEach
    void setUp() {

        instructorUser = CurrentUser.builder()
                .id("INS_001")
                .role(Role.INSTRUCTOR)
                .status(Status.ACTIVE)
                .build();

        studentUser = CurrentUser.builder()
                .id("STU_001")
                .role(Role.STUDENT)
                .status(Status.ACTIVE)
                .build();

        inactiveInstructor = CurrentUser.builder()
                .id("INS_002")
                .role(Role.INSTRUCTOR)
                .status(Status.INACTIVE)
                .build();

        item1 = RequestItem.builder()
                .id("ITEM_1")
                .componentId("COMP_1")
                .quantityRequested(5)
                .quantityApproved(3)
                .status(RequestItemStatus.PENDING)
                .build();

        item2 = RequestItem.builder()
                .id("ITEM_2")
                .componentId("COMP_2")
                .quantityRequested(2)
                .quantityApproved(1)
                .status(RequestItemStatus.PENDING)
                .build();

        request = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .status(RequestStatus.PENDING)
                .items(new ArrayList<>(List.of(item1, item2)))
                .build();
    }
    
    @Test
    void rejectRequest_success() {

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        RequestResponseDTO response =
                requestService.rejectRequest(
                        "REQ_1",
                        instructorUser
                );

        assertNotNull(response);

        assertEquals(
                RequestStatus.REJECTED,
                request.getStatus()
        );

        for (RequestItem item : request.getItems()) {

            assertEquals(
                    RequestItemStatus.REJECTED,
                    item.getStatus()
            );

            assertEquals(
                    0,
                    item.getQuantityApproved()
            );
        }

        verify(requestRepository)
                .findById("REQ_1");
    }
    
    @Test
    void rejectRequest_studentForbidden() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.rejectRequest(
                        "REQ_1",
                        studentUser
                )
        );

        verify(requestRepository, never())
                .findById(any());
    }
    
    @Test
    void rejectRequest_inactiveInstructorForbidden() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.rejectRequest(
                        "REQ_1",
                        inactiveInstructor
                )
        );

        verify(requestRepository, never())
                .findById(any());
    }
    
    @Test
    void rejectRequest_notFound() {

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> requestService.rejectRequest(
                        "REQ_1",
                        instructorUser
                )
        );
    }
    
    @Test
    void rejectRequest_alreadyProcessed() {

        request.setStatus(RequestStatus.APPROVED);

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        assertThrows(
                ConflictException.class,
                () -> requestService.rejectRequest(
                        "REQ_1",
                        instructorUser
                )
        );
    }
    
    @Test
    void rejectRequest_noItems() {

        request.setItems(List.of());

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        assertThrows(
                ConflictException.class,
                () -> requestService.rejectRequest(
                        "REQ_1",
                        instructorUser
                )
        );
    }
    
    @Test
    void rejectRequest_resetsAllApprovedQuantities() {

        item1.setQuantityApproved(5);
        item2.setQuantityApproved(2);

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        requestService.rejectRequest(
                "REQ_1",
                instructorUser
        );

        assertEquals(0, item1.getQuantityApproved());
        assertEquals(0, item2.getQuantityApproved());

        assertEquals(
                RequestItemStatus.REJECTED,
                item1.getStatus()
        );

        assertEquals(
                RequestItemStatus.REJECTED,
                item2.getStatus()
        );
    }
    
    @Test
    void rejectRequest_updatesRequestStatus() {

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        requestService.rejectRequest(
                "REQ_1",
                instructorUser
        );

        assertEquals(
                RequestStatus.REJECTED,
                request.getStatus()
        );
    }
    
    @Test
    void rejectRequest_unauthorized_neverTouchesRepository() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.rejectRequest(
                        "REQ_1",
                        studentUser
                )
        );

        verify(requestRepository, never())
                .findById(any());
    }
}
