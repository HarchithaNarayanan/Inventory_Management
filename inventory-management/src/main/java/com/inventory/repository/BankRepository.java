package com.inventory.repository;

import com.inventory.entity.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BankRepository — Spring Data JPA repository for {@link Bank} entity.
 */
@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    /** Checks for duplicate bank code on create */
    boolean existsByBankCode(String bankCode);

    /** Checks for duplicate account number on create */
    boolean existsByAccountNo(String accountNo);

    /** Checks for code conflict on update (excludes current bank) */
    boolean existsByBankCodeAndBankIdNot(String bankCode, Long bankId);

    /** Checks for account number conflict on update */
    boolean existsByAccountNoAndBankIdNot(String accountNo, Long bankId);

    /** Find by unique code */
    Optional<Bank> findByBankCode(String bankCode);

    @Query("SELECT e FROM Bank e WHERE e.status = 'ACTIVE' AND (:search IS NULL OR LOWER(e.bankName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.bankCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Bank> findBySearchAndPagination(@Param("search") String search, Pageable pageable);
}
