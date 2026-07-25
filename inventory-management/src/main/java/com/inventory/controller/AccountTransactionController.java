package com.inventory.controller;

import com.inventory.dto.AccountTransactionDto;
import com.inventory.dto.ApiResponse;
import com.inventory.service.AccountTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-transactions")
@RequiredArgsConstructor
public class AccountTransactionController {

    private final AccountTransactionService accountTransactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountTransactionDto>> createTransaction(@Valid @RequestBody AccountTransactionDto dto) {
        AccountTransactionDto saved = accountTransactionService.createTransaction(dto);
        return new ResponseEntity<>(
                ApiResponse.success("Transaction created successfully", saved),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountTransactionDto>>> getAllTransactions() {
        List<AccountTransactionDto> transactions = accountTransactionService.getAllTransactions();
        return ResponseEntity.ok(
                ApiResponse.success("Transactions fetched successfully", transactions)
        );
    }
}
