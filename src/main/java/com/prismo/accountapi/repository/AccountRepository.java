package com.prismo.accountapi.repository;

import com.prismo.accountapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find account by document number
     * @param documentNumber the document number to search for
     * @return Optional containing the account if found
     */
    Optional<Account> findByDocumentNumber(String documentNumber);

    /**
     * Check if account exists by document number
     * @param documentNumber the document number to check
     * @return true if account exists, false otherwise
     */
    boolean existsByDocumentNumber(String documentNumber);
}
