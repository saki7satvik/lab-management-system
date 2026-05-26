package com.issue_service.service;

import java.util.List;

import com.issue_service.dto.request.CreateIssueDTO;
import com.issue_service.dto.response.CurrentUser;
import com.issue_service.dto.response.IssueResponseDTO;

public interface IssueService {
	IssueResponseDTO issueComponents(CreateIssueDTO dto, CurrentUser currentUser);

	IssueResponseDTO getIssueById(String issueId, CurrentUser currentUser);

	List<IssueResponseDTO> getIssuesByRequestId(String requestId, CurrentUser currentUser);

	List<IssueResponseDTO> getAllIssues(CurrentUser currentUser);

//	List<IssueResponseDTO> getMyIssues(CurrentUser currentUser);
}
