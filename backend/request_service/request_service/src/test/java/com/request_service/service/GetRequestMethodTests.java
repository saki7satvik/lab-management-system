package com.request_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.request_service.enums.Role;
import com.request_service.enums.Status;
import com.request_service.exception.ResourceNotFoundException;
import com.request_service.exception.UnauthorizedException;
import com.request_service.repository.RequestRepository;
import com.request_service.service.impl.RequestServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GetRequestMethodTests {
	
	@Mock
    private RequestRepository requestRepository;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private RequestServiceImpl requestService;
    
    private CurrentUser studentUser;
    private CurrentUser anotherStudent;
    private CurrentUser instructorUser;
    private CurrentUser adminUser;
    private CurrentUser inactiveUser;
    
    @BeforeEach
    void setUp() {

        studentUser = CurrentUser.builder()
                .id("STU_001")
                .role(Role.STUDENT)
                .status(Status.ACTIVE)
                .build();

        anotherStudent = CurrentUser.builder()
                .id("STU_999")
                .role(Role.STUDENT)
                .status(Status.ACTIVE)
                .build();

        instructorUser = CurrentUser.builder()
                .id("INS_001")
                .role(Role.INSTRUCTOR)
                .status(Status.ACTIVE)
                .build();

        adminUser = CurrentUser.builder()
                .id("ADM_001")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        inactiveUser = CurrentUser.builder()
                .id("STU_002")
                .role(Role.STUDENT)
                .status(Status.INACTIVE)
                .build();
    }
    
    @Test
    void getRequestById_owner_success() {

        Request request = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .build();

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        RequestResponseDTO response =
                requestService.getRequestById(
                        "REQ_1",
                        studentUser
                );

        assertNotNull(response);

        verify(requestRepository)
                .findById("REQ_1");
    }
    
    @Test
    void getRequestById_instructor_success() {

        Request request = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .build();

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        RequestResponseDTO response =
                requestService.getRequestById(
                        "REQ_1",
                        instructorUser
                );

        assertNotNull(response);

        verify(requestRepository)
                .findById("REQ_1");
    }
    
    @Test
    void getRequestById_studentForbidden() {

        Request request = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .build();

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.of(request));

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.getRequestById(
                        "REQ_1",
                        anotherStudent
                )
        );
    }
    
    @Test
    void getRequestById_inactiveUser_forbidden() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.getRequestById(
                        "REQ_1",
                        inactiveUser
                )
        );

        verify(requestRepository, never())
                .findById(any());
    }
    
    @Test
    void getRequestById_notFound() {

        when(requestRepository.findById("REQ_1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> requestService.getRequestById(
                        "REQ_1",
                        adminUser
                )
        );
    }
    
    @Test
    void getRequestsByUser_self_success() {

        Request request = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .build();

        when(requestRepository.findByCreatedBy("STU_001"))
                .thenReturn(List.of(request));

        List<RequestResponseDTO> responses =
                requestService.getRequestsByUser(
                        "STU_001",
                        studentUser
                );

        assertEquals(1, responses.size());

        verify(requestRepository)
                .findByCreatedBy("STU_001");
    }
    
    @Test
    void getRequestsByUser_instructor_success() {

        Request request = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .build();

        when(requestRepository.findByCreatedBy("STU_001"))
                .thenReturn(List.of(request));

        List<RequestResponseDTO> responses =
                requestService.getRequestsByUser(
                        "STU_001",
                        instructorUser
                );

        assertEquals(1, responses.size());

        verify(requestRepository)
                .findByCreatedBy("STU_001");
    }
    
    @Test
    void getRequestsByUser_studentForbidden() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.getRequestsByUser(
                        "STU_001",
                        anotherStudent
                )
        );

        verify(requestRepository, never())
                .findByCreatedBy(any());
    }
    
    @Test
    void getRequestsByUser_inactiveUser_forbidden() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.getRequestsByUser(
                        "STU_001",
                        inactiveUser
                )
        );

        verify(requestRepository, never())
                .findByCreatedBy(any());
    }
    
    @Test
    void getAllRequests_admin_success() {

        Request request1 = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .build();

        Request request2 = Request.builder()
                .id("REQ_2")
                .createdBy("STU_002")
                .build();

        when(requestRepository.findAll())
                .thenReturn(List.of(request1, request2));

        List<RequestResponseDTO> responses =
                requestService.getAllRequests(adminUser);

        assertEquals(2, responses.size());

        verify(requestRepository)
                .findAll();
    }
    
    @Test
    void getAllRequests_instructor_success() {

        Request request = Request.builder()
                .id("REQ_1")
                .createdBy("STU_001")
                .build();

        when(requestRepository.findAll())
                .thenReturn(List.of(request));

        List<RequestResponseDTO> responses =
                requestService.getAllRequests(instructorUser);

        assertEquals(1, responses.size());

        verify(requestRepository)
                .findAll();
    }
    
    @Test
    void getAllRequests_studentForbidden() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.getAllRequests(studentUser)
        );

        verify(requestRepository, never())
                .findAll();
    }
    
    @Test
    void getAllRequests_inactiveUser_forbidden() {

        assertThrows(
                UnauthorizedException.class,
                () -> requestService.getAllRequests(inactiveUser)
        );

        verify(requestRepository, never())
                .findAll();
    }
    
    @Test
    void getAllRequests_emptyList() {

        when(requestRepository.findAll())
                .thenReturn(List.of());

        List<RequestResponseDTO> responses =
                requestService.getAllRequests(adminUser);

        assertTrue(responses.isEmpty());
    }

}
