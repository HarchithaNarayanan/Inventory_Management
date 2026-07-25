package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.ReportDto;
import com.inventory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * ReportController — REST Controller for all Business Reports.
 *
 * <p>Base URL: {@code /api/reports}</p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>GET /api/reports/invoice/{billingId}               — Invoice Report</li>
 *   <li>GET /api/reports/sales-ledger?from=&to=            — Sales Ledger</li>
 *   <li>GET /api/reports/purchase-ledger?from=&to=         — Purchase Ledger (Receipts)</li>
 *   <li>GET /api/reports/stock-ledger?itemId=&from=&to=    — Stock Ledger</li>
 *   <li>GET /api/reports/trial-balance?asOf=               — Trial Balance</li>
 *   <li>GET /api/reports/profit-loss?from=&to=             — Profit & Loss Statement</li>
 * </ul>
 *
 * <p>All date parameters use format {@code yyyy-MM-dd} (ISO date format).</p>
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // ----------------------------------------------------------------
    // 1. INVOICE REPORT
    // GET /api/reports/invoice/{billingId}
    // ----------------------------------------------------------------

    /**
     * Retrieves the full invoice report for a specific bill.
     *
     * @param billingId the billing ID
     * @return Invoice Report with all line items and totals
     */
    @GetMapping("/invoice/{billingId}")
    public ResponseEntity<ApiResponse<ReportDto.InvoiceReport>> getInvoiceReport(
            @PathVariable Long billingId) {

        return ResponseEntity.ok(
                ApiResponse.success("Invoice report generated successfully",
                        reportService.getInvoiceReport(billingId)));
    }

    // ----------------------------------------------------------------
    // 2. SALES LEDGER
    // GET /api/reports/sales-ledger?from=2026-01-01&to=2026-12-31
    // ----------------------------------------------------------------

    /**
     * Generates a Sales Ledger for all invoices in a date range.
     *
     * @param from start date in yyyy-MM-dd format
     * @param to   end date in yyyy-MM-dd format
     * @return Sales Ledger report with entries and grand totals
     */
    @GetMapping("/sales-ledger")
    public ResponseEntity<ApiResponse<ReportDto.SalesLedgerReport>> getSalesLedger(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success("Sales ledger generated successfully",
                        reportService.getSalesLedger(from, to)));
    }

    // ----------------------------------------------------------------
    // 3. PURCHASE LEDGER (Receipts)
    // GET /api/reports/purchase-ledger?from=2026-01-01&to=2026-12-31
    // ----------------------------------------------------------------

    /**
     * Generates a Purchase Ledger (receipts) for a date range.
     *
     * @param from start date in yyyy-MM-dd format
     * @param to   end date in yyyy-MM-dd format
     * @return Purchase Ledger report
     */
    @GetMapping("/purchase-ledger")
    public ResponseEntity<ApiResponse<ReportDto.PurchaseLedgerReport>> getPurchaseLedger(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success("Purchase ledger generated successfully",
                        reportService.getPurchaseLedger(from, to)));
    }

    // ----------------------------------------------------------------
    // 4. STOCK LEDGER
    // GET /api/reports/stock-ledger?itemId=1&from=2026-01-01&to=2026-12-31
    // ----------------------------------------------------------------

    /**
     * Generates a Stock Ledger for a specific item in a date range.
     *
     * @param itemId item ID to generate stock movements for
     * @param from   start date in yyyy-MM-dd format
     * @param to     end date in yyyy-MM-dd format
     * @return Stock Ledger with running balances
     */
    @GetMapping("/stock-ledger")
    public ResponseEntity<ApiResponse<ReportDto.StockLedgerReport>> getStockLedger(
            @RequestParam("itemId") Long itemId,
            @RequestParam("from")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success("Stock ledger generated successfully",
                        reportService.getStockLedger(itemId, from, to)));
    }

    // ----------------------------------------------------------------
    // 5. TRIAL BALANCE
    // GET /api/reports/trial-balance?asOf=2026-12-31
    // ----------------------------------------------------------------

    /**
     * Generates a Trial Balance as of a specific date.
     * Shows outstanding amounts per customer.
     *
     * @param asOf the "as of" date in yyyy-MM-dd format
     * @return Trial Balance report with totals
     */
    @GetMapping("/trial-balance")
    public ResponseEntity<ApiResponse<ReportDto.TrialBalanceReport>> getTrialBalance(
            @RequestParam("asOf") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {

        return ResponseEntity.ok(
                ApiResponse.success("Trial balance generated successfully",
                        reportService.getTrialBalance(asOf)));
    }

    // ----------------------------------------------------------------
    // 5.5 SALES TAX LEDGER
    // GET /api/reports/sales-tax-ledger?from=2026-01-01&to=2026-12-31
    // ----------------------------------------------------------------

    /**
     * Generates a Sales Tax Ledger for a given period.
     */
    @GetMapping("/sales-tax-ledger")
    public ResponseEntity<ApiResponse<ReportDto.SalesTaxLedgerReport>> getSalesTaxLedger(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success("Sales tax ledger generated successfully",
                        reportService.getSalesTaxLedger(from, to)));
    }

    // ----------------------------------------------------------------
    // 6. PROFIT & LOSS STATEMENT
    // GET /api/reports/profit-loss?from=2026-01-01&to=2026-12-31
    // ----------------------------------------------------------------

    /**
     * Generates a Profit & Loss Statement for a given period.
     *
     * @param from start date in yyyy-MM-dd format
     * @param to   end date in yyyy-MM-dd format
     * @return Profit & Loss report with revenue, COGS, and gross profit
     */
    @GetMapping("/profit-loss")
    public ResponseEntity<ApiResponse<ReportDto.ProfitLossReport>> getProfitLoss(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success("Profit & Loss statement generated successfully",
                        reportService.getProfitLossStatement(from, to)));
    }
}
