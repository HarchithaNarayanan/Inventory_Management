package com.inventory.repository;

import com.inventory.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByBillDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByBillNo(String billNo);

    @Query("SELECT e FROM Purchase e WHERE (:search IS NULL OR LOWER(e.billNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.supplier.supplierName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Purchase> findBySearchAndPagination(@Param("search") String search, Pageable pageable);
}
