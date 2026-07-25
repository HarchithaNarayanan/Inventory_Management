package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ReportDto — Collection of inner DTOs for all 6 report types.
 *
 * <p>These DTOs are used exclusively by {@link com.inventory.service.ReportService}
 * and {@link com.inventory.controller.ReportController} to return structured
 * report data without exposing raw entity objects.</p>
 */
public class ReportDto {

    // ================================================================
    // 1. INVOICE REPORT
    // ================================================================

    /**
     * Full invoice report for a specific billing/bill.
     * Used by: GET /api/reports/invoice/{billingId}
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class InvoiceReport {
        private String billNo;
        private LocalDate billDate;
        private String customerName;
        private String customerAddress;
        private String customerGstNo;
        private List<InvoiceLine> lines;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal netAmount;
        private String paymentStatus;
    }

    /** One line on the invoice */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class InvoiceLine {
        private String itemCode;
        private String itemName;
        private String unit;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountPercent;
        private BigDecimal gstPercent;
        private BigDecimal lineTotal;
    }

    // ================================================================
    // 2. SALES LEDGER
    // ================================================================

    /**
     * Sales Ledger entry — one row per invoice.
     * Used by: GET /api/reports/sales-ledger?from=&to=
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SalesLedgerEntry {
        private LocalDate billDate;
        private String billNo;
        private String customerName;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal netAmount;
        private String paymentStatus;
    }

    /** Summary totals for the sales ledger report */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SalesLedgerReport {
        private LocalDate fromDate;
        private LocalDate toDate;
        private List<SalesLedgerEntry> entries;
        private BigDecimal grandTotalAmount;
        private BigDecimal grandNetAmount;
        private int totalInvoices;
    }

    // ================================================================
    // 3. PURCHASE LEDGER
    // ================================================================

    /**
     * Purchase Ledger entry — tracks materials purchased from suppliers.
     * Used by: GET /api/reports/purchase-ledger?from=&to=
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PurchaseLedgerEntry {
        private LocalDate purchaseDate;
        private String billNo;
        private String supplierName;
        private BigDecimal totalAmount;
        private BigDecimal taxAmount;
        private BigDecimal netAmount;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PurchaseLedgerReport {
        private LocalDate fromDate;
        private LocalDate toDate;
        private List<PurchaseLedgerEntry> entries;
        private BigDecimal grandTotalAmount;
        private BigDecimal grandNetAmount;
        private int totalPurchases;
    }

    // ================================================================
    // 3.5 SALES TAX LEDGER
    // ================================================================

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SalesTaxLedgerEntry {
        private LocalDate date;
        private String billNo;
        private String customerName;
        private BigDecimal taxableAmount;
        private BigDecimal taxAmount;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SalesTaxLedgerReport {
        private LocalDate fromDate;
        private LocalDate toDate;
        private List<SalesTaxLedgerEntry> entries;
        private BigDecimal totalTaxableAmount;
        private BigDecimal totalTaxCollected;
    }

    // ================================================================
    // 4. STOCK LEDGER
    // ================================================================

    /**
     * Stock Ledger entry — one movement record (opening + each sale).
     * Used by: GET /api/reports/stock-ledger?itemId=&from=&to=
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StockLedgerEntry {
        private LocalDate date;
        private String reference;     // bill number or "Opening"
        private BigDecimal inQty;     // goods received (for future purchase module)
        private BigDecimal outQty;    // goods sold
        private BigDecimal balance;   // running balance
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StockLedgerReport {
        private Long itemId;
        private String itemCode;
        private String itemName;
        private String unit;
        private LocalDate fromDate;
        private LocalDate toDate;
        private List<StockLedgerEntry> entries;
        private BigDecimal closingStock;
    }

    // ================================================================
    // 5. TRIAL BALANCE
    // ================================================================

    /**
     * Trial Balance entry — one row per customer showing debit/credit/balance.
     * Used by: GET /api/reports/trial-balance?asOf=
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TrialBalanceEntry {
        private String customerCode;
        private String customerName;
        private BigDecimal openingBalance;
        private BigDecimal totalSales;      // total billed amount
        private BigDecimal totalReceived;   // total payments received
        private BigDecimal closingBalance;  // openingBalance + totalSales - totalReceived
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TrialBalanceReport {
        private LocalDate asOfDate;
        private List<TrialBalanceEntry> entries;
        private BigDecimal totalDebit;
        private BigDecimal totalCredit;
        private BigDecimal netBalance;
    }

    // ================================================================
    // 6. PROFIT & LOSS STATEMENT
    // ================================================================

    /**
     * Profit & Loss Statement.
     * Revenue = sum of net billing amounts in the period
     * COGS    = sum of (quantity × purchaseRate) for all items sold
     * Gross Profit = Revenue - COGS
     * Used by: GET /api/reports/profit-loss?from=&to=
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ProfitLossReport {
        private LocalDate fromDate;
        private LocalDate toDate;
        private BigDecimal totalRevenue;       // Sum of netAmount from all bills
        private BigDecimal totalCogs;          // Sum of (qty × purchaseRate)
        private BigDecimal grossProfit;        // totalRevenue - totalCogs
        private BigDecimal grossProfitPercent; // (grossProfit / totalRevenue) × 100
        private int       totalInvoices;
        private BigDecimal totalDiscount;      // Total discounts given
    }
}
