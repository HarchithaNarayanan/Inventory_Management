package com.inventory.service;

import com.inventory.dto.AccountTransactionDto;
import com.inventory.entity.AccountTransaction;
import com.inventory.entity.Bank;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.AccountTransactionRepository;
import com.inventory.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final AccountTransactionRepository accountTransactionRepository;
    private final BankRepository bankRepository;

    public AccountTransactionDto createTransaction(AccountTransactionDto dto) {
        AccountTransaction transaction = AccountTransaction.builder()
                .transactionDate(dto.getTransactionDate())
                .transactionType(dto.getTransactionType())
                .referenceNo(dto.getReferenceNo())
                .amount(dto.getAmount())
                .drCr(dto.getDrCr())
                .remarks(dto.getRemarks())
                .build();

        if (dto.getBankId() != null) {
            Bank bank = bankRepository.findById(dto.getBankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bank", "id", dto.getBankId()));
            transaction.setBank(bank);
        }

        AccountTransaction saved = accountTransactionRepository.save(transaction);
        return mapToDto(saved);
    }

    public List<AccountTransactionDto> getAllTransactions() {
        return accountTransactionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AccountTransactionDto mapToDto(AccountTransaction t) {
        AccountTransactionDto dto = AccountTransactionDto.builder()
                .transactionId(t.getTransactionId())
                .transactionDate(t.getTransactionDate())
                .transactionType(t.getTransactionType())
                .referenceNo(t.getReferenceNo())
                .amount(t.getAmount())
                .drCr(t.getDrCr())
                .remarks(t.getRemarks())
                .build();

        if (t.getBank() != null) {
            dto.setBankId(t.getBank().getBankId());
            dto.setBankName(t.getBank().getBankName());
        }
        return dto;
    }
}
