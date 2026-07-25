package com.inventory.service;

import com.inventory.dto.ItemRequestDto;
import com.inventory.dto.ItemResponseDto;
import com.inventory.entity.Item;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public ItemResponseDto createItem(ItemRequestDto requestDto) {
        if (itemRepository.existsByItemCode(requestDto.getItemCode())) {
            throw new DuplicateResourceException("Item", "itemCode", requestDto.getItemCode());
        }
        Item item = mapToEntity(requestDto);
        item.setActive(true);
        return mapToResponseDto(itemRepository.save(item));
    }

    public ItemResponseDto updateItem(Long itemId, ItemRequestDto requestDto) {
        Item existing = itemRepository.findByItemIdAndIsActiveTrue(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemId));

        if (itemRepository.existsByItemCodeAndItemIdNot(requestDto.getItemCode(), itemId)) {
            throw new DuplicateResourceException("Item", "itemCode", requestDto.getItemCode());
        }

        updateEntityFromDto(existing, requestDto);
        return mapToResponseDto(itemRepository.save(existing));
    }

    public void deleteItem(Long itemId) {
        Item existing = itemRepository.findByItemIdAndIsActiveTrue(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemId));
        existing.setActive(false);
        itemRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Long itemId) {
        return mapToResponseDto(itemRepository.findByItemIdAndIsActiveTrue(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemId)));
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllItems() {
        return itemRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ItemResponseDto> getPaginatedEntities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("itemId").descending());
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return itemRepository.findBySearchAndPagination(query, pageable).map(this::mapToResponseDto);
    }

    private Item mapToEntity(ItemRequestDto dto) {
        return Item.builder()
                .itemCode(dto.getItemCode())
                .itemName(dto.getItemName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .unitOfMeasure(dto.getUnitOfMeasure())
                .unitOfRate(dto.getUnitOfRate())
                .grade(dto.getGrade())
                .purchaseGl(dto.getPurchaseGl())
                .salesGl(dto.getSalesGl())
                .entryId(dto.getEntryId())
                .enteredBy(dto.getEnteredBy())
                .modifiedId(dto.getModifiedId())
                .modifiedBy(dto.getModifiedBy())
                .manufactureDate(parseDate(dto.getManufactureDate()))
                .expiryDate(parseDate(dto.getExpiryDate()))
                .entryDate(parseDate(dto.getEntryDate()))
                .purchaseRate(nullSafe(dto.getPurchaseRate()))
                .sellingRate(nullSafe(dto.getSellingRate()))
                .gstPercent(nullSafe(dto.getGstPercent()))
                .openingStock(nullSafe(dto.getOpeningStock()))
                .reorderLevel(nullSafe(dto.getReorderLevel()))
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .isActive(true)
                .build();
    }

    private void updateEntityFromDto(Item entity, ItemRequestDto dto) {
        entity.setItemCode(dto.getItemCode());
        entity.setItemName(dto.getItemName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setUnitOfMeasure(dto.getUnitOfMeasure());
        entity.setUnitOfRate(dto.getUnitOfRate());
        entity.setGrade(dto.getGrade());
        entity.setPurchaseGl(dto.getPurchaseGl());
        entity.setSalesGl(dto.getSalesGl());
        entity.setEntryId(dto.getEntryId());
        entity.setEnteredBy(dto.getEnteredBy());
        entity.setModifiedId(dto.getModifiedId());
        entity.setModifiedBy(dto.getModifiedBy());
        entity.setManufactureDate(parseDate(dto.getManufactureDate()));
        entity.setExpiryDate(parseDate(dto.getExpiryDate()));
        entity.setEntryDate(parseDate(dto.getEntryDate()));
        entity.setPurchaseRate(nullSafe(dto.getPurchaseRate()));
        entity.setSellingRate(nullSafe(dto.getSellingRate()));
        entity.setGstPercent(nullSafe(dto.getGstPercent()));
        entity.setOpeningStock(nullSafe(dto.getOpeningStock()));
        entity.setReorderLevel(nullSafe(dto.getReorderLevel()));
        entity.setStatus(dto.getStatus());
    }

    private ItemResponseDto mapToResponseDto(Item item) {
        return ItemResponseDto.builder()
                .itemId(item.getItemId())
                .itemCode(item.getItemCode())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .category(item.getCategory())
                .unitOfMeasure(item.getUnitOfMeasure())
                .unitOfRate(item.getUnitOfRate())
                .grade(item.getGrade())
                .purchaseGl(item.getPurchaseGl())
                .salesGl(item.getSalesGl())
                .entryId(item.getEntryId())
                .enteredBy(item.getEnteredBy())
                .modifiedId(item.getModifiedId())
                .modifiedBy(item.getModifiedBy())
                .manufactureDate(item.getManufactureDate())
                .expiryDate(item.getExpiryDate())
                .entryDate(item.getEntryDate())
                .purchaseRate(item.getPurchaseRate())
                .sellingRate(item.getSellingRate())
                .gstPercent(item.getGstPercent())
                .openingStock(item.getOpeningStock())
                .reorderLevel(item.getReorderLevel())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            return null; // fallback or handle properly
        }
    }
}
