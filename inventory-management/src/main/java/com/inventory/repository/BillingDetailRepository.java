package com.inventory.repository;

import com.inventory.entity.BillingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BillingDetailRepository — Spring Data JPA repository for {@link BillingDetail} entity.
 */
@Repository
public interface BillingDetailRepository extends JpaRepository<BillingDetail, Long> {

    /** Finds all line items for a given billing (used in stock ledger calculations) */
    List<BillingDetail> findByBilling_BillingId(Long billingId);

    /** Finds all sales lines for a specific item — used in stock ledger report */
    List<BillingDetail> findByItem_ItemId(Long itemId);
}
