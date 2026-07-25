package com.inventory.service;

import com.inventory.dto.ReceiptRequestDto;
import com.inventory.dto.ReceiptResponseDto;
import com.inventory.entity.*;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReceiptService — Business logic for Customer Payment Receipt operations.
 *
 * <p>Handles receipt creation with bill allocations.
 * Validates customer, bank (if provided), and each bill allocation.</p>
 */
@Service
@Transactional
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BillingRepository billingRepository;

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    /**
     * Creates a new receipt with bill allocations.
     *
     * @param requestDto the receipt data
     * @return saved receipt as response DTO
     * @throws DuplicateResourceException if receiptNo already exists
     * @throws ResourceNotFoundException  if customer, bank, or any bill not found
     */
    public ReceiptResponseDto createReceipt(ReceiptRequestDto requestDto) {

        // Validate unique receipt number
        if (receiptRepository.existsByReceiptNo(requestDto.getReceiptNo())) {
            throw new DuplicateResourceException("Receipt", "receiptNo", requestDto.getReceiptNo());
        }

        // Validate customer
        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", "id", requestDto.getCustomerId()));

        // Validate bank (optional — null for CASH payments)
        Bank bank = null;
        if (requestDto.getBankId() != null) {
            bank = bankRepository.findById(requestDto.getBankId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Bank", "id", requestDto.getBankId()));
        }

        // Build receipt entity
        Receipt receipt = Receipt.builder()
                .receiptNo(requestDto.getReceiptNo())
                .receiptDate(requestDto.getReceiptDate())
                .customer(customer)
                .bank(bank)
                .paymentMode(requestDto.getPaymentMode())
                .chequeNo(requestDto.getChequeNo())
                .chequeDate(requestDto.getChequeDate())
                .totalAmount(requestDto.getTotalAmount())
                .remarks(requestDto.getRemarks())
                .receiptDetails(new ArrayList<>())
                .build();

        // Add bill allocations if provided
        if (requestDto.getReceiptDetails() != null) {
            for (ReceiptRequestDto.ReceiptDetailRequestDto detailDto
                    : requestDto.getReceiptDetails()) {

                Billing billing = billingRepository.findById(detailDto.getBillingId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Billing", "id", detailDto.getBillingId()));

                ReceiptDetail detail = ReceiptDetail.builder()
                        .receipt(receipt)
                        .billing(billing)
                        .allocatedAmount(detailDto.getAllocatedAmount())
                        .build();

                receipt.getReceiptDetails().add(detail);
            }
        }

        return mapToResponseDto(receiptRepository.save(receipt));
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    /**
     * Updates an existing receipt by ID.
     *
     * @param receiptId  ID of the receipt to update
     * @param requestDto updated data
     * @return updated receipt response DTO
     */
    public ReceiptResponseDto updateReceipt(Long receiptId, ReceiptRequestDto requestDto) {

        Receipt existing = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));

        if (receiptRepository.existsByReceiptNoAndReceiptIdNot(requestDto.getReceiptNo(), receiptId)) {
            throw new DuplicateResourceException("Receipt", "receiptNo", requestDto.getReceiptNo());
        }

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", "id", requestDto.getCustomerId()));

        Bank bank = null;
        if (requestDto.getBankId() != null) {
            bank = bankRepository.findById(requestDto.getBankId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Bank", "id", requestDto.getBankId()));
        }

        // Update header fields
        existing.setReceiptNo(requestDto.getReceiptNo());
        existing.setReceiptDate(requestDto.getReceiptDate());
        existing.setCustomer(customer);
        existing.setBank(bank);
        existing.setPaymentMode(requestDto.getPaymentMode());
        existing.setChequeNo(requestDto.getChequeNo());
        existing.setChequeDate(requestDto.getChequeDate());
        existing.setTotalAmount(requestDto.getTotalAmount());
        existing.setRemarks(requestDto.getRemarks());

        // Replace details
        existing.getReceiptDetails().clear();

        if (requestDto.getReceiptDetails() != null) {
            for (ReceiptRequestDto.ReceiptDetailRequestDto detailDto
                    : requestDto.getReceiptDetails()) {

                Billing billing = billingRepository.findById(detailDto.getBillingId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Billing", "id", detailDto.getBillingId()));

                existing.getReceiptDetails().add(ReceiptDetail.builder()
                        .receipt(existing)
                        .billing(billing)
                        .allocatedAmount(detailDto.getAllocatedAmount())
                        .build());
            }
        }

        return mapToResponseDto(receiptRepository.save(existing));
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    /**
     * Deletes a receipt and all its allocations.
     *
     * @param receiptId ID of the receipt to delete
     */
    public void deleteReceipt(Long receiptId) {
        if (!receiptRepository.existsById(receiptId)) {
            throw new ResourceNotFoundException("Receipt", "id", receiptId);
        }
        receiptRepository.deleteById(receiptId);
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    /** Gets a receipt by primary key */
    @Transactional(readOnly = true)
    public ReceiptResponseDto getReceiptById(Long receiptId) {
        return mapToResponseDto(receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId)));
    }

    /** Gets all receipts */
    @Transactional(readOnly = true)
    public List<ReceiptResponseDto> getAllReceipts() {
        return receiptRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    private ReceiptResponseDto mapToResponseDto(Receipt receipt) {

        List<ReceiptResponseDto.ReceiptDetailResponseDto> detailDtos =
                receipt.getReceiptDetails().stream()
                        .map(d -> ReceiptResponseDto.ReceiptDetailResponseDto.builder()
                                .recDetailId(d.getRecDetailId())
                                .billingId(d.getBilling().getBillingId())
                                .billNo(d.getBilling().getBillNo())
                                .allocatedAmount(d.getAllocatedAmount())
                                .build())
                        .collect(Collectors.toList());

        return ReceiptResponseDto.builder()
                .receiptId(receipt.getReceiptId())
                .receiptNo(receipt.getReceiptNo())
                .receiptDate(receipt.getReceiptDate())
                .customerId(receipt.getCustomer().getCustomerId())
                .customerName(receipt.getCustomer().getCustomerName())
                .bankId(receipt.getBank() != null ? receipt.getBank().getBankId() : null)
                .bankName(receipt.getBank() != null ? receipt.getBank().getBankName() : null)
                .paymentMode(receipt.getPaymentMode())
                .chequeNo(receipt.getChequeNo())
                .chequeDate(receipt.getChequeDate())
                .totalAmount(receipt.getTotalAmount())
                .remarks(receipt.getRemarks())
                .receiptDetails(detailDtos)
                .createdAt(receipt.getCreatedAt())
                .updatedAt(receipt.getUpdatedAt())
                .build();
    }
}
