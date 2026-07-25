package com.inventory.repository;

import com.inventory.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * CustomerRepository — Spring Data JPA repository for {@link Customer} entity.
 *
 * <p>Provides CRUD operations plus custom query methods for business logic validation.</p>
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /** Checks if a customer code exists (used for duplicate validation on create) */
    boolean existsByCustomerCode(String customerCode);

    /** Checks if a code exists for a DIFFERENT customer (used on update) */
    boolean existsByCustomerCodeAndCustomerIdNot(String customerCode, Long customerId);

    /** Finds a customer by unique code */
    Optional<Customer> findByCustomerCode(String customerCode);

    /** Returns active customers */
    List<Customer> findByIsActiveTrue();
    Optional<Customer> findByCustomerIdAndIsActiveTrue(Long customerId);

    /** Returns customers filtered by status */
    List<Customer> findByStatus(String status);

    @Query("SELECT e FROM Customer e WHERE e.isActive = true AND (:search IS NULL OR LOWER(e.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.customerCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Customer> findBySearchAndPagination(@Param("search") String search, Pageable pageable);
}
