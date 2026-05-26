package com.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory_service.entity.ComponentHistory;

public interface ComponentHistoryRepository extends JpaRepository<ComponentHistory, String>{

}
