package com.inventory.repository;

import com.inventory.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ReceiptRepository — Spring Data JPA repository for {@link Receipt} entity.
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    /** Checks for duplicate receipt number on create */
    boolean existsByReceiptNo(String receiptNo);

    /** Checks for receipt number conflict on update */
    boolean existsByReceiptNoAndReceiptIdNot(String receiptNo, Long receiptId);

    /** Finds a receipt by its unique number */
    Optional<Receipt> findByReceiptNo(String receiptNo);

    /** Finds all receipts for a customer */
    List<Receipt> findByCustomer_CustomerId(Long customerId);

    /**
     * Finds receipts within a date range — used for Purchase/Sales Ledger and Trial Balance.
     */
    @Query("SELECT r FROM Receipt r WHERE r.receiptDate BETWEEN :fromDate AND :toDate ORDER BY r.receiptDate")
    List<Receipt> findByReceiptDateBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
