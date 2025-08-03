package com.prismo.accountapi.repository;

import com.prismo.accountapi.entity.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {

    /**
     * Find operation type by description
     * @param description the description to search for
     * @return Optional containing the operation type if found
     */
    Optional<OperationType> findByDescription(String description);
}
