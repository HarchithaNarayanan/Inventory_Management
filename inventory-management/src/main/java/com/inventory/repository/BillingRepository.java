package com.inventory.repository;

import com.inventory.entity.Billing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * BillingRepository — Spring Data JPA repository for {@link Billing} entity.
 *
 * <p>Uses JPQL queries for report generation (Sales Ledger, Invoice lookup).</p>
 */
@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    /** Checks for duplicate bill number on create */
    boolean existsByBillNo(String billNo);

    /** Checks for bill number conflict on update */
    boolean existsByBillNoAndBillingIdNot(String billNo, Long billingId);

    /** Finds a bill by its unique number */
    Optional<Billing> findByBillNo(String billNo);

    /** Finds all bills for a specific customer */
    List<Billing> findByCustomer_CustomerId(Long customerId);

    /**
     * Finds all bills within a date range — used for Sales Ledger report.
     *
     * @param fromDate start date (inclusive)
     * @param toDate   end date (inclusive)
     * @return list of bills ordered by date
     */
    @Query("SELECT b FROM Billing b WHERE b.billDate BETWEEN :fromDate AND :toDate ORDER BY b.billDate")
    List<Billing> findByBillDateBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Finds unpaid/partially paid bills for a customer — used for receipt allocation.
     *
     * @param customerId  the customer ID
     * @param statusList  list of statuses to filter (PENDING, PARTIAL)
     * @return outstanding bills
     */
    @Query("SELECT b FROM Billing b WHERE b.customer.customerId = :customerId " +
           "AND b.paymentStatus IN :statusList ORDER BY b.billDate")
    List<Billing> findOutstandingBillsByCustomer(
            @Param("customerId") Long customerId,
            @Param("statusList") List<String> statusList);

    @Query("SELECT e FROM Billing e WHERE (:search IS NULL OR LOWER(e.billNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.customer.customerName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Billing> findBySearchAndPagination(@Param("search") String search, Pageable pageable);
}
