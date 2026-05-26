package com.inventory_service.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventory_service.dto.response.ComponentResponseDTO;
import com.inventory_service.entity.Component;
import com.inventory_service.exception.ResourceNotFoundException;
import com.inventory_service.repository.ComponentHistoryRepository;
import com.inventory_service.repository.InventoryRepository;
import com.inventory_service.service.impl.InventoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class GetComponentTests {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ComponentHistoryRepository componentHistoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;
    
    private Component component(
            String id,
            String name,
            boolean active
    ) {

        Component component =
                new Component();

        component.setId(id);
        component.setName(name);
        component.setDescription("Test Component");

        component.setTotalQuantity(10);
        component.setAvailableQuantity(8);

        component.setActive(active);

        return component;
    }
    
    @Test
    void getAllComponents_success() {

        Component active1 =
                component("COMP_001", "ESP32", true);

        Component active2 =
                component("COMP_002", "Arduino", true);

        Component inactive =
                component("COMP_003", "Sensor", false);

        when(inventoryRepository.findAll())
                .thenReturn(
                        List.of(active1, active2, inactive)
                );

        List<ComponentResponseDTO> response =
                inventoryService.getAllComponents();

        assertEquals(2, response.size());

        assertEquals(
                "ESP32",
                response.get(0).getName()
        );

        assertEquals(
                "Arduino",
                response.get(1).getName()
        );
    }
    
    @Test
    void getAllComponents_empty() {

        when(inventoryRepository.findAll())
                .thenReturn(List.of());

        List<ComponentResponseDTO> response =
                inventoryService.getAllComponents();

        assertNotNull(response);

        assertTrue(response.isEmpty());
    }
    
    @Test
    void getComponentById_success() {

        Component component =
                component("COMP_001", "ESP32", true);

        when(inventoryRepository.findByIdAndActiveTrue("COMP_001"))
                .thenReturn(Optional.of(component));

        ComponentResponseDTO response =
                inventoryService.getComponentById(
                        "COMP_001"
                );

        assertNotNull(response);

        assertEquals(
                "COMP_001",
                response.getId()
        );

        assertEquals(
                "ESP32",
                response.getName()
        );
    }
    
    @Test
    void getComponentById_notFound() {

        when(inventoryRepository.findByIdAndActiveTrue("COMP_001"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.getComponentById(
                        "COMP_001"
                )
        );
    }
    
    @Test
    void getComponentById_inactive() {

        when(inventoryRepository.findByIdAndActiveTrue("COMP_001"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.getComponentById(
                        "COMP_001"
                )
        );
    }

    
}
