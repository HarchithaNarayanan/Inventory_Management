package com.inventory.repository;

import com.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Duplicate checks
    boolean existsBySupplierCode(String supplierCode);
    boolean existsBySupplierCodeAndSupplierIdNot(String supplierCode, Long supplierId);

    // Fetch only active suppliers
    List<Supplier> findByIsActiveTrue();

    // Fetch a single active supplier
    Optional<Supplier> findBySupplierIdAndIsActiveTrue(Long supplierId);

    @Query("SELECT e FROM Supplier e WHERE e.isActive = true AND (:search IS NULL OR LOWER(e.supplierName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.supplierCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Supplier> findBySearchAndPagination(@Param("search") String search, Pageable pageable);
}
