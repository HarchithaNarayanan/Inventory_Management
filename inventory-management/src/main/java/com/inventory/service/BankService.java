package com.inventory.service;

import com.inventory.dto.BankRequestDto;
import com.inventory.dto.BankResponseDto;
import com.inventory.entity.Bank;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BankService — Business logic for Bank Master operations.
 *
 * <p>Validates uniqueness of both bank code AND account number on create/update.
 * Both fields are independently unique constraints in the database.</p>
 */
@Service
@Transactional
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    /**
     * Creates a new bank account.
     * Validates uniqueness of both bankCode and accountNo.
     *
     * @param requestDto incoming bank data
     * @return saved bank as response DTO
     * @throws DuplicateResourceException if bankCode or accountNo already exists
     */
    public BankResponseDto createBank(BankRequestDto requestDto) {
        // Check bank code uniqueness
        if (bankRepository.existsByBankCode(requestDto.getBankCode())) {
            throw new DuplicateResourceException("Bank", "bankCode", requestDto.getBankCode());
        }
        // Check account number uniqueness
        if (bankRepository.existsByAccountNo(requestDto.getAccountNo())) {
            throw new DuplicateResourceException("Bank", "accountNo", requestDto.getAccountNo());
        }
        return mapToResponseDto(bankRepository.save(mapToEntity(requestDto)));
    }

    /**
     * Updates an existing bank by ID.
     *
     * @param bankId     ID of the bank to update
     * @param requestDto updated data
     * @return updated bank as response DTO
     */
    public BankResponseDto updateBank(Long bankId, BankRequestDto requestDto) {
        Bank existing = bankRepository.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", "id", bankId));

        if (bankRepository.existsByBankCodeAndBankIdNot(requestDto.getBankCode(), bankId)) {
            throw new DuplicateResourceException("Bank", "bankCode", requestDto.getBankCode());
        }
        if (bankRepository.existsByAccountNoAndBankIdNot(requestDto.getAccountNo(), bankId)) {
            throw new DuplicateResourceException("Bank", "accountNo", requestDto.getAccountNo());
        }

        updateEntityFromDto(existing, requestDto);
        return mapToResponseDto(bankRepository.save(existing));
    }

    /**
     * Deletes a bank by ID.
     *
     * @param bankId ID of the bank to delete
     * @throws ResourceNotFoundException if not found
     */
    public void deleteBank(Long bankId) {
        if (!bankRepository.existsById(bankId)) {
            throw new ResourceNotFoundException("Bank", "id", bankId);
        }
        bankRepository.deleteById(bankId);
    }

    /** Gets bank by primary key */
    @Transactional(readOnly = true)
    public BankResponseDto getBankById(Long bankId) {
        return mapToResponseDto(bankRepository.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", "id", bankId)));
    }

    /** Gets all banks */
    @Transactional(readOnly = true)
    public List<BankResponseDto> getAllBanks() {
        return bankRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<BankResponseDto> getPaginatedEntities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bankId").descending());
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return bankRepository.findBySearchAndPagination(query, pageable).map(this::mapToResponseDto);
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    private Bank mapToEntity(BankRequestDto dto) {
        return Bank.builder()
                .bankCode(dto.getBankCode())
                .bankName(dto.getBankName())
                .accountNo(dto.getAccountNo())
                .accountType(dto.getAccountType())
                .ifscCode(dto.getIfscCode())
                .bankBranch(dto.getBankBranch())
                .limitAmount(dto.getLimitAmount() != null ? dto.getLimitAmount() : BigDecimal.ZERO)
                .glCode(dto.getGlCode())
                .openingBalance(dto.getOpeningBalance() != null
                        ? dto.getOpeningBalance() : BigDecimal.ZERO)
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();
    }

    private void updateEntityFromDto(Bank entity, BankRequestDto dto) {
        entity.setBankCode(dto.getBankCode());
        entity.setBankName(dto.getBankName());
        entity.setAccountNo(dto.getAccountNo());
        entity.setAccountType(dto.getAccountType());
        entity.setIfscCode(dto.getIfscCode());
        entity.setBankBranch(dto.getBankBranch());
        if (dto.getLimitAmount() != null) entity.setLimitAmount(dto.getLimitAmount());
        entity.setGlCode(dto.getGlCode());
        entity.setOpeningBalance(dto.getOpeningBalance() != null
                ? dto.getOpeningBalance() : BigDecimal.ZERO);
        entity.setStatus(dto.getStatus());
    }

    private BankResponseDto mapToResponseDto(Bank bank) {
        return BankResponseDto.builder()
                .bankId(bank.getBankId())
                .bankCode(bank.getBankCode())
                .bankName(bank.getBankName())
                .accountNo(bank.getAccountNo())
                .accountType(bank.getAccountType())
                .ifscCode(bank.getIfscCode())
                .bankBranch(bank.getBankBranch())
                .limitAmount(bank.getLimitAmount())
                .glCode(bank.getGlCode())
                .openingBalance(bank.getOpeningBalance())
                .status(bank.getStatus())
                .createdAt(bank.getCreatedAt())
                .updatedAt(bank.getUpdatedAt())
                .build();
    }
}
