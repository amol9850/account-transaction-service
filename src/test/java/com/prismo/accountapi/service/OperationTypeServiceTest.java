package com.prismo.accountapi.service;

import com.prismo.accountapi.entity.OperationType;
import com.prismo.accountapi.repository.OperationTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OperationTypeServiceTest {

    @Mock
    private OperationTypeRepository operationTypeRepository;

    @InjectMocks
    private OperationTypeService operationTypeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findOperationTypeById_Success() {
        OperationType operationType = new OperationType();
        operationType.setOperationTypeId(1L);
        operationType.setDescription("PAYMENT");

        when(operationTypeRepository.findById(1L)).thenReturn(Optional.of(operationType));

        OperationType result = operationTypeService.findOperationTypeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOperationTypeId());
        assertEquals("PAYMENT", result.getDescription());

        verify(operationTypeRepository).findById(1L);
    }

    @Test
    void findOperationTypeById_NotFound_ThrowsException() {
        when(operationTypeRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            operationTypeService.findOperationTypeById(99L);
        });

        assertTrue(ex.getMessage().contains("not found"));

        verify(operationTypeRepository).findById(99L);
    }
}
