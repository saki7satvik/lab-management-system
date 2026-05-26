package com.issue_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.issue_service.dto.request.CreateIssueDTO;
import com.issue_service.dto.request.IssueItemDTO;
import com.issue_service.dto.response.CurrentUser;
import com.issue_service.dto.response.IssueResponseDTO;
import com.issue_service.dto.response.RequestItemResponseDTO;
import com.issue_service.dto.response.RequestResponseDTO;
import com.issue_service.entity.Issue;
import com.issue_service.entity.IssueItem;
import com.issue_service.enums.IssueStatus;
import com.issue_service.enums.RequestStatus;
import com.issue_service.enums.Role;
import com.issue_service.enums.Status;
import com.issue_service.exception.BadRequestException;
import com.issue_service.exception.ConflictException;
import com.issue_service.exception.ResourceNotFoundException;
import com.issue_service.exception.ServiceUnavailableException;
import com.issue_service.exception.UnauthorizedException;
import com.issue_service.repository.IssueRepository;
import com.issue_service.service.impl.IssueServiceImpl;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
public class IssueServiceGetMethodTest {
	
	@Mock
	private InventoryClient inventoryClient;
	
	@Mock
	private RequestClient requestClient;
	
	@Mock
	private IssueRepository issueRepository;
	
	@InjectMocks
	private IssueServiceImpl issueService;
	
	private CurrentUser studentUser;
	private CurrentUser anotherStudent;
	private CurrentUser instructorUser;
	private CurrentUser adminUser;
	private CurrentUser inactiveStudent;
	
	private Issue issue;
	private IssueItem issueItem;
	
	private RequestResponseDTO requestResponse;
	
	@BeforeEach
	void setUp() {

	    studentUser = CurrentUser.builder()
	            .id("STU_001")
	            .role(Role.STUDENT)
	            .status(Status.ACTIVE)
	            .build();

	    anotherStudent = CurrentUser.builder()
	            .id("STU_002")
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

	    inactiveStudent = CurrentUser.builder()
	            .id("STU_003")
	            .role(Role.STUDENT)
	            .status(Status.INACTIVE)
	            .build();

	    issueItem = IssueItem.builder()
	            .requestItemId("REQ_ITEM_1")
	            .componentId("COMP_1")
	            .quantityIssued(2)
	            .build();

	    issue = Issue.builder()
	            .id("ISSUE_1")
	            .requestId("REQ_001")
	            .issuedBy("INS_001")
	            .status(IssueStatus.ISSUED)
	            .items(List.of(issueItem))
	            .build();

	    requestResponse = RequestResponseDTO.builder()
	            .id("REQ_001")
	            .createdBy("STU_001")
	            .status(RequestStatus.APPROVED)
	            .build();
	}
	
	@Test
	void getIssueById_studentOwnIssue_success() {

	    when(issueRepository.findById("ISSUE_1"))
	            .thenReturn(Optional.of(issue));

	    when(requestClient.getRequestById("REQ_001"))
	            .thenReturn(requestResponse);

	    IssueResponseDTO response =
	            issueService.getIssueById(
	                    "ISSUE_1",
	                    studentUser
	            );

	    assertNotNull(response);

	    verify(issueRepository)
	            .findById("ISSUE_1");
	}
	
	@Test
	void getIssueById_instructorAccess_success() {

	    when(issueRepository.findById("ISSUE_1"))
	            .thenReturn(Optional.of(issue));

	    IssueResponseDTO response =
	            issueService.getIssueById(
	                    "ISSUE_1",
	                    instructorUser
	            );

	    assertNotNull(response);

	    verify(requestClient, never())
	            .getRequestById(any());
	}
	
	@Test
	void getIssueById_adminAccess_success() {

	    when(issueRepository.findById("ISSUE_1"))
	            .thenReturn(Optional.of(issue));

	    IssueResponseDTO response =
	            issueService.getIssueById(
	                    "ISSUE_1",
	                    adminUser
	            );

	    assertNotNull(response);
	}
	
	@Test
	void getIssueById_studentForbidden() {

	    when(issueRepository.findById("ISSUE_1"))
	            .thenReturn(Optional.of(issue));

	    when(requestClient.getRequestById("REQ_001"))
	            .thenReturn(requestResponse);

	    assertThrows(
	            UnauthorizedException.class,
	            () -> issueService.getIssueById(
	                    "ISSUE_1",
	                    anotherStudent
	            )
	    );
	}
	
	@Test
	void getIssueById_notFound() {

	    when(issueRepository.findById("ISSUE_1"))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            ResourceNotFoundException.class,
	            () -> issueService.getIssueById(
	                    "ISSUE_1",
	                    studentUser
	            )
	    );
	}
	
	@Test
	void getIssueById_inactiveUserForbidden() {

	    assertThrows(
	            UnauthorizedException.class,
	            () -> issueService.getIssueById(
	                    "ISSUE_1",
	                    inactiveStudent
	            )
	    );

	    verify(issueRepository, never())
	            .findById(any());
	}
	
	@Test
	void getIssuesByRequestId_studentOwnIssues_success() {

	    when(issueRepository.findByRequestId("REQ_001"))
	            .thenReturn(List.of(issue));

	    when(requestClient.getRequestById("REQ_001"))
	            .thenReturn(requestResponse);

	    List<IssueResponseDTO> responses =
	            issueService.getIssuesByRequestId(
	                    "REQ_001",
	                    studentUser
	            );

	    assertEquals(1, responses.size());
	}
	
	@Test
	void getIssuesByRequestId_studentForbidden() {

	    when(issueRepository.findByRequestId("REQ_001"))
	            .thenReturn(List.of(issue));

	    when(requestClient.getRequestById("REQ_001"))
	            .thenReturn(requestResponse);

	    assertThrows(
	            UnauthorizedException.class,
	            () -> issueService.getIssuesByRequestId(
	                    "REQ_001",
	                    anotherStudent
	            )
	    );
	}
	
	@Test
	void getIssuesByRequestId_instructorAccess_success() {

	    when(issueRepository.findByRequestId("REQ_001"))
	            .thenReturn(List.of(issue));

	    List<IssueResponseDTO> responses =
	            issueService.getIssuesByRequestId(
	                    "REQ_001",
	                    instructorUser
	            );

	    assertEquals(1, responses.size());

	    verify(requestClient, never())
	            .getRequestById(any());
	}
	
	@Test
	void getIssuesByRequestId_emptyList() {

	    when(issueRepository.findByRequestId("REQ_001"))
	            .thenReturn(List.of());

	    List<IssueResponseDTO> responses =
	            issueService.getIssuesByRequestId(
	                    "REQ_001",
	                    studentUser
	            );

	    assertTrue(responses.isEmpty());
	}
	
	@Test
	void getIssuesByRequestId_inactiveUserForbidden() {

	    assertThrows(
	            UnauthorizedException.class,
	            () -> issueService.getIssuesByRequestId(
	                    "REQ_001",
	                    inactiveStudent
	            )
	    );

	    verify(issueRepository, never())
	            .findByRequestId(any());
	}
	
	@Test
	void getAllIssues_instructorSuccess() {

	    when(issueRepository.findAll())
	            .thenReturn(List.of(issue));

	    List<IssueResponseDTO> responses =
	            issueService.getAllIssues(
	                    instructorUser
	            );

	    assertEquals(1, responses.size());

	    verify(issueRepository)
	            .findAll();
	}
	
	@Test
	void getAllIssues_adminSuccess() {

	    when(issueRepository.findAll())
	            .thenReturn(List.of(issue));

	    List<IssueResponseDTO> responses =
	            issueService.getAllIssues(
	                    adminUser
	            );

	    assertEquals(1, responses.size());
	}
	
	@Test
	void getAllIssues_studentForbidden() {

	    assertThrows(
	            UnauthorizedException.class,
	            () -> issueService.getAllIssues(
	                    studentUser
	            )
	    );

	    verify(issueRepository, never())
	            .findAll();
	}
	
	@Test
	void getAllIssues_inactiveUserForbidden() {

	    assertThrows(
	            UnauthorizedException.class,
	            () -> issueService.getAllIssues(
	                    inactiveStudent
	            )
	    );

	    verify(issueRepository, never())
	            .findAll();
	}
}
