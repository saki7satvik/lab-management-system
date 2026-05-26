package com.request_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.request_service.entity.Request;

public interface RequestRepository extends JpaRepository<Request, String> {

	List<Request> findByCreatedBy(String userId);

}
