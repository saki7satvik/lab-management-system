package com.inventory_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory_service.entity.Component;

public interface InventoryRepository extends JpaRepository<Component, String> {

	boolean existsByName(String name);
	
	List<Component> findAllById(Iterable<String> ids);

	Optional<Component> findByIdAndActiveTrue(String id);

}
