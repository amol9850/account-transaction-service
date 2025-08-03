package com.prismo.accountapi.service;

import com.prismo.accountapi.entity.OperationType;
import com.prismo.accountapi.repository.OperationTypeRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OperationTypeService {

    Logger logger = org.apache.logging.log4j.LogManager.getLogger(OperationTypeService.class);

    @Autowired
    private OperationTypeRepository operationTypeRepository;


    /**
     * Find operation type by ID (for internal use)
     * @param operationTypeId the operation type ID
     * @return OperationType entity
     * @throws RuntimeException if operation type not found
     */
    @Transactional(readOnly = true)
    public OperationType findOperationTypeById(Long operationTypeId) {
        return operationTypeRepository.findById(operationTypeId)
                .map(operationType -> {
                    logger.info("Retrieved operation type with ID: {}", operationTypeId);
                    return operationType;
                })
            .orElseThrow(() ->{ logger.error("Operation type not found with ID: {}", operationTypeId);
                return new RuntimeException("Operation type not found with ID: " + operationTypeId);
            });
    }

}
