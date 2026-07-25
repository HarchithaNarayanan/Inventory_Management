package com.inventory.repository;

import com.inventory.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompanyCodeAndIsActiveTrue(String companyCode);
    List<Company> findByIsActiveTrue();
    boolean existsByCompanyCodeAndIsActiveTrue(String companyCode);

    @Query("SELECT e FROM Company e WHERE e.isActive = true AND (:search IS NULL OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.companyCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Company> findBySearchAndPagination(@Param("search") String search, Pageable pageable);
}
