package com.inventory.repository;

import com.inventory.entity.ReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReceiptDetailRepository — Spring Data JPA repository for {@link ReceiptDetail} entity.
 */
@Repository
public interface ReceiptDetailRepository extends JpaRepository<ReceiptDetail, Long> {

    /** Finds all allocations for a receipt */
    List<ReceiptDetail> findByReceipt_ReceiptId(Long receiptId);

    /** Finds all receipt allocations applied to a specific bill */
    List<ReceiptDetail> findByBilling_BillingId(Long billingId);
}
