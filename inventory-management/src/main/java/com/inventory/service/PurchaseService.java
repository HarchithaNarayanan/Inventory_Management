package com.inventory.service;

import com.inventory.dto.PurchaseRequestDto;
import com.inventory.dto.PurchaseResponseDto;
import com.inventory.entity.Item;
import com.inventory.entity.Purchase;
import com.inventory.entity.PurchaseDetail;
import com.inventory.entity.Supplier;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.PurchaseRepository;
import com.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public PurchaseResponseDto createPurchase(PurchaseRequestDto requestDto) {
        if (purchaseRepository.existsByBillNo(requestDto.getBillNo())) {
            throw new IllegalArgumentException("Bill number already exists");
        }

        Supplier supplier = supplierRepository.findById(requestDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", requestDto.getSupplierId()));

        Purchase purchase = Purchase.builder()
                .billNo(requestDto.getBillNo())
                .billDate(requestDto.getBillDate())
                .supplier(supplier)
                .remarks(requestDto.getRemarks())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = requestDto.getTaxAmount() != null ? requestDto.getTaxAmount() : BigDecimal.ZERO;

        for (PurchaseRequestDto.PurchaseDetailDto detailDto : requestDto.getPurchaseDetails()) {
            Item item = itemRepository.findById(detailDto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item", "id", detailDto.getItemId()));

            BigDecimal gstPercent = detailDto.getGstPercent() != null ? detailDto.getGstPercent() : BigDecimal.ZERO;
            BigDecimal lineTotal = detailDto.getQuantity().multiply(detailDto.getUnitPrice())
                    .setScale(2, RoundingMode.HALF_UP);
            
            // Add GST to line total if we want lineTotal to include GST, or keep it separate.
            // Usually, quantity * unit_price = taxable value.
            BigDecimal gstAmount = lineTotal.multiply(gstPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal lineNetTotal = lineTotal.add(gstAmount);

            totalAmount = totalAmount.add(lineTotal);
            // If tax amount wasn't provided at header, accumulate it from lines
            if (requestDto.getTaxAmount() == null) {
                taxAmount = taxAmount.add(gstAmount);
            }

            PurchaseDetail detail = PurchaseDetail.builder()
                    .purchase(purchase)
                    .item(item)
                    .quantity(detailDto.getQuantity())
                    .unitPrice(detailDto.getUnitPrice())
                    .gstPercent(gstPercent)
                    .lineTotal(lineNetTotal)
                    .build();

            purchase.getPurchaseDetails().add(detail);
        }

        purchase.setTotalAmount(totalAmount);
        purchase.setTaxAmount(taxAmount);
        purchase.setNetAmount(totalAmount.add(taxAmount));

        Purchase savedPurchase = purchaseRepository.save(purchase);

        // --- Stock Auto-Update: increment openingStock for each purchased item ---
        for (PurchaseDetail detail : savedPurchase.getPurchaseDetails()) {
            Item item = detail.getItem();
            item.setOpeningStock(item.getOpeningStock().add(detail.getQuantity()));
            itemRepository.save(item);
        }

        return mapToResponseDto(savedPurchase);
    }

    public List<PurchaseResponseDto> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PurchaseResponseDto> getPaginatedEntities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseId").descending());
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return purchaseRepository.findBySearchAndPagination(query, pageable).map(this::mapToResponseDto);
    }

    public PurchaseResponseDto getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", id));
        return mapToResponseDto(purchase);
    }

    private PurchaseResponseDto mapToResponseDto(Purchase purchase) {
        List<PurchaseResponseDto.PurchaseDetailResponseDto> detailDtos = purchase.getPurchaseDetails().stream()
                .map(detail -> PurchaseResponseDto.PurchaseDetailResponseDto.builder()
                        .detailId(detail.getDetailId())
                        .itemId(detail.getItem().getItemId())
                        .itemCode(detail.getItem().getItemCode())
                        .itemName(detail.getItem().getItemName())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .gstPercent(detail.getGstPercent())
                        .lineTotal(detail.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return PurchaseResponseDto.builder()
                .purchaseId(purchase.getPurchaseId())
                .billNo(purchase.getBillNo())
                .billDate(purchase.getBillDate())
                .supplierId(purchase.getSupplier().getSupplierId())
                .supplierCode(purchase.getSupplier().getSupplierCode())
                .supplierName(purchase.getSupplier().getSupplierName())
                .totalAmount(purchase.getTotalAmount())
                .taxAmount(purchase.getTaxAmount())
                .netAmount(purchase.getNetAmount())
                .remarks(purchase.getRemarks())
                .purchaseDetails(detailDtos)
                .createdAt(purchase.getCreatedAt())
                .updatedAt(purchase.getUpdatedAt())
                .build();
    }
}
