package com.inventory.repository;

import com.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ItemRepository — Spring Data JPA repository for {@link Item} entity.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /** Checks for duplicate item code on create */
    boolean existsByItemCode(String itemCode);

    /** Checks for code conflict on update (excludes current item) */
    boolean existsByItemCodeAndItemIdNot(String itemCode, Long itemId);

    /** Find by unique code */
    Optional<Item> findByItemCode(String itemCode);

    /** Find active items */
    List<Item> findByIsActiveTrue();
    Optional<Item> findByItemIdAndIsActiveTrue(Long itemId);

    /** Filter by status */
    List<Item> findByStatus(String status);

    /** Filter by category */
    List<Item> findByCategory(String category);

    @Query("SELECT e FROM Item e WHERE e.isActive = true AND (:search IS NULL OR LOWER(e.itemName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.itemCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Item> findBySearchAndPagination(@Param("search") String search, Pageable pageable);
}
