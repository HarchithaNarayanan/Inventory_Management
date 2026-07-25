package com.inventory.service;

import com.inventory.dto.BillingRequestDto;
import com.inventory.dto.BillingResponseDto;
import com.inventory.entity.Billing;
import com.inventory.entity.BillingDetail;
import com.inventory.entity.Customer;
import com.inventory.entity.Item;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.BillingRepository;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * BillingService — Business logic for Sales Invoice (Billing) operations.
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Validates customer existence before creating a bill</li>
 *   <li>Validates each line item's existence</li>
 *   <li>Auto-calculates line totals, total amount, tax amount, net amount</li>
 *   <li>Saves header + detail in one atomic transaction</li>
 * </ul>
 *
 * <p>Line total formula:
 * <pre>
 *   baseAmount    = quantity × unitPrice
 *   afterDiscount = baseAmount × (1 - discountPercent/100)
 *   gstAmount     = afterDiscount × (gstPercent/100)
 *   lineTotal     = afterDiscount + gstAmount
 * </pre>
 * </p>
 */
@Service
@Transactional
public class BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    /**
     * Creates a new billing/invoice with all its line items.
     * Automatically calculates totals from line items.
     *
     * @param requestDto the bill header + line items
     * @return saved billing as response DTO
     * @throws DuplicateResourceException if billNo already exists
     * @throws ResourceNotFoundException  if customer or any item not found
     */
    public BillingResponseDto createBilling(BillingRequestDto requestDto) {

        // Validate unique bill number
        if (billingRepository.existsByBillNo(requestDto.getBillNo())) {
            throw new DuplicateResourceException("Billing", "billNo", requestDto.getBillNo());
        }

        // Validate customer exists
        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", "id", requestDto.getCustomerId()));

        // Build and calculate billing
        Billing billing = buildBillingFromRequest(requestDto, customer);

        Billing savedBilling = billingRepository.save(billing);

        // --- Stock Auto-Update: decrement openingStock for each sold item ---
        for (BillingDetail detail : savedBilling.getBillingDetails()) {
            Item item = detail.getItem();
            item.setOpeningStock(item.getOpeningStock().subtract(detail.getQuantity()));
            itemRepository.save(item);
        }

        return mapToResponseDto(savedBilling);
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    /**
     * Updates an existing bill by ID.
     * Replaces all existing line items with the new ones (orphanRemoval handles deletion).
     *
     * @param billingId  ID of the bill to update
     * @param requestDto updated bill data
     * @return updated billing response DTO
     */
    public BillingResponseDto updateBilling(Long billingId, BillingRequestDto requestDto) {

        Billing existing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", billingId));

        if (billingRepository.existsByBillNoAndBillingIdNot(requestDto.getBillNo(), billingId)) {
            throw new DuplicateResourceException("Billing", "billNo", requestDto.getBillNo());
        }

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", "id", requestDto.getCustomerId()));

        // Clear existing details (orphanRemoval = true will delete them from DB)
        existing.getBillingDetails().clear();

        // Rebuild from request
        existing.setBillNo(requestDto.getBillNo());
        existing.setBillDate(requestDto.getBillDate());
        existing.setCustomer(customer);
        existing.setPaymentStatus(requestDto.getPaymentStatus() != null
                ? requestDto.getPaymentStatus() : existing.getPaymentStatus());
        existing.setRemarks(requestDto.getRemarks());

        // Rebuild and add new line items
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxAmount   = BigDecimal.ZERO;

        for (BillingRequestDto.BillingDetailRequestDto detailDto : requestDto.getBillingDetails()) {
            Item item = itemRepository.findById(detailDto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Item", "id", detailDto.getItemId()));

            BillingDetail detail = buildDetail(existing, item, detailDto);
            existing.getBillingDetails().add(detail);
            totalAmount = totalAmount.add(detail.getLineTotal());

            // Accumulate GST
            BigDecimal gstPct = detailDto.getGstPercent() != null
                    ? detailDto.getGstPercent() : BigDecimal.ZERO;
            BigDecimal lineGst = detail.getQuantity()
                    .multiply(detail.getUnitPrice())
                    .multiply(gstPct)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            taxAmount = taxAmount.add(lineGst);
        }

        BigDecimal discount = requestDto.getDiscountAmount() != null
                ? requestDto.getDiscountAmount() : BigDecimal.ZERO;

        existing.setTotalAmount(totalAmount);
        existing.setDiscountAmount(discount);
        existing.setTaxAmount(taxAmount);
        existing.setNetAmount(totalAmount.subtract(discount));

        return mapToResponseDto(billingRepository.save(existing));
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    /**
     * Deletes a bill and all its line items (cascade delete).
     *
     * @param billingId ID of the bill to delete
     */
    public void deleteBilling(Long billingId) {
        if (!billingRepository.existsById(billingId)) {
            throw new ResourceNotFoundException("Billing", "id", billingId);
        }
        billingRepository.deleteById(billingId);
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    /** Gets a bill by primary key */
    @Transactional(readOnly = true)
    public BillingResponseDto getBillingById(Long billingId) {
        return mapToResponseDto(billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", billingId)));
    }

    /** Gets all bills */
    @Transactional(readOnly = true)
    public List<BillingResponseDto> getAllBillings() {
        return billingRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<BillingResponseDto> getPaginatedEntities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("billingId").descending());
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return billingRepository.findBySearchAndPagination(query, pageable).map(this::mapToResponseDto);
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    /**
     * Constructs a Billing entity from request DTO, calculates all totals.
     */
    private Billing buildBillingFromRequest(BillingRequestDto requestDto, Customer customer) {

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxAmount   = BigDecimal.ZERO;

        // Build the parent billing entity first
        Billing billing = Billing.builder()
                .billNo(requestDto.getBillNo())
                .billDate(requestDto.getBillDate())
                .customer(customer)
                .paymentStatus(requestDto.getPaymentStatus() != null
                        ? requestDto.getPaymentStatus() : "PENDING")
                .remarks(requestDto.getRemarks())
                .build();

        // Process each line item
        for (BillingRequestDto.BillingDetailRequestDto detailDto : requestDto.getBillingDetails()) {

            Item item = itemRepository.findById(detailDto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Item", "id", detailDto.getItemId()));

            BillingDetail detail = buildDetail(billing, item, detailDto);
            billing.getBillingDetails().add(detail);

            totalAmount = totalAmount.add(detail.getLineTotal());

            // Accumulate GST amount for tax_amount column
            BigDecimal gstPct = detailDto.getGstPercent() != null
                    ? detailDto.getGstPercent() : BigDecimal.ZERO;
            BigDecimal baseAfterDiscount = detailDto.getQuantity()
                    .multiply(detailDto.getUnitPrice())
                    .multiply(BigDecimal.ONE.subtract(
                            (detailDto.getDiscountPercent() != null
                                    ? detailDto.getDiscountPercent() : BigDecimal.ZERO)
                                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
            taxAmount = taxAmount.add(
                    baseAfterDiscount.multiply(gstPct)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        }

        BigDecimal discount = requestDto.getDiscountAmount() != null
                ? requestDto.getDiscountAmount() : BigDecimal.ZERO;

        billing.setTotalAmount(totalAmount);
        billing.setDiscountAmount(discount);
        billing.setTaxAmount(taxAmount);
        billing.setNetAmount(totalAmount.subtract(discount));

        return billing;
    }

    /**
     * Creates a BillingDetail entity from a line item DTO.
     * Calculates lineTotal = qty × price × (1 - disc%) × (1 + gst%)
     */
    private BillingDetail buildDetail(Billing billing, Item item,
                                      BillingRequestDto.BillingDetailRequestDto dto) {

        BigDecimal discPct = dto.getDiscountPercent() != null ? dto.getDiscountPercent() : BigDecimal.ZERO;
        BigDecimal gstPct  = dto.getGstPercent() != null ? dto.getGstPercent() : BigDecimal.ZERO;

        BigDecimal base           = dto.getQuantity().multiply(dto.getUnitPrice());
        BigDecimal afterDiscount  = base.multiply(
                BigDecimal.ONE.subtract(discPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
        BigDecimal gstAmount      = afterDiscount.multiply(gstPct
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal lineTotal      = afterDiscount.add(gstAmount).setScale(2, RoundingMode.HALF_UP);

        return BillingDetail.builder()
                .billing(billing)
                .item(item)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .discountPercent(discPct)
                .gstPercent(gstPct)
                .lineTotal(lineTotal)
                .build();
    }

    /**
     * Maps a Billing entity (with details) to a BillingResponseDto.
     */
    private BillingResponseDto mapToResponseDto(Billing billing) {

        List<BillingResponseDto.BillingDetailResponseDto> detailDtos =
                billing.getBillingDetails().stream()
                        .map(d -> BillingResponseDto.BillingDetailResponseDto.builder()
                                .detailId(d.getDetailId())
                                .itemId(d.getItem().getItemId())
                                .itemName(d.getItem().getItemName())
                                .quantity(d.getQuantity())
                                .unitPrice(d.getUnitPrice())
                                .discountPercent(d.getDiscountPercent())
                                .gstPercent(d.getGstPercent())
                                .lineTotal(d.getLineTotal())
                                .build())
                        .collect(Collectors.toList());

        return BillingResponseDto.builder()
                .billingId(billing.getBillingId())
                .billNo(billing.getBillNo())
                .billDate(billing.getBillDate())
                .customerId(billing.getCustomer().getCustomerId())
                .customerName(billing.getCustomer().getCustomerName())
                .totalAmount(billing.getTotalAmount())
                .discountAmount(billing.getDiscountAmount())
                .taxAmount(billing.getTaxAmount())
                .netAmount(billing.getNetAmount())
                .paymentStatus(billing.getPaymentStatus())
                .remarks(billing.getRemarks())
                .billingDetails(detailDtos)
                .createdAt(billing.getCreatedAt())
                .updatedAt(billing.getUpdatedAt())
                .build();
    }
}
