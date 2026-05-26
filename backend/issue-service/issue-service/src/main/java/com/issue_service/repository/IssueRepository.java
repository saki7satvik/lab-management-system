package com.issue_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.issue_service.entity.Issue;

public interface IssueRepository extends JpaRepository<Issue, String>{
	List<Issue> findByRequestId(String requestId);
	List<Issue> findByRequestIdIn(List<String> requestIds);
}
