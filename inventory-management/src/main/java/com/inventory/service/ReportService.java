package com.inventory.service;

import com.inventory.dto.ReportDto;
import com.inventory.entity.*;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    // 1. INVOICE REPORT
    public ReportDto.InvoiceReport getInvoiceReport(Long billingId) {
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", billingId));

        Customer customer = billing.getCustomer();
        String address = String.join(", ",
                Arrays.stream(new String[]{
                        customer.getAddressLine1(),
                        customer.getAddressLine2(),
                        customer.getCity(),
                        customer.getPincode()})
                        .filter(s -> s != null && !s.isEmpty())
                        .toArray(String[]::new));

        List<ReportDto.InvoiceLine> lines = billing.getBillingDetails().stream()
                .map(detail -> ReportDto.InvoiceLine.builder()
                        .itemCode(detail.getItem().getItemCode())
                        .itemName(detail.getItem().getItemName())
                        .unit(detail.getItem().getUnitOfMeasure())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .discountPercent(detail.getDiscountPercent())
                        .gstPercent(detail.getGstPercent())
                        .lineTotal(detail.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return ReportDto.InvoiceReport.builder()
                .billNo(billing.getBillNo())
                .billDate(billing.getBillDate())
                .customerName(customer.getCustomerName())
                .customerAddress(address)
                .customerGstNo(customer.getGstNo())
                .lines(lines)
                .totalAmount(billing.getTotalAmount())
                .discountAmount(billing.getDiscountAmount())
                .taxAmount(billing.getTaxAmount())
                .netAmount(billing.getNetAmount())
                .paymentStatus(billing.getPaymentStatus())
                .build();
    }

    // 2. SALES LEDGER
    public ReportDto.SalesLedgerReport getSalesLedger(LocalDate fromDate, LocalDate toDate) {
        List<Billing> billings = billingRepository.findByBillDateBetween(fromDate, toDate);

        List<ReportDto.SalesLedgerEntry> entries = billings.stream()
                .map(b -> ReportDto.SalesLedgerEntry.builder()
                        .billDate(b.getBillDate())
                        .billNo(b.getBillNo())
                        .customerName(b.getCustomer().getCustomerName())
                        .totalAmount(b.getTotalAmount())
                        .discountAmount(b.getDiscountAmount())
                        .taxAmount(b.getTaxAmount())
                        .netAmount(b.getNetAmount())
                        .paymentStatus(b.getPaymentStatus())
                        .build())
                .collect(Collectors.toList());

        BigDecimal grandTotal = entries.stream().map(ReportDto.SalesLedgerEntry::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal grandNet = entries.stream().map(ReportDto.SalesLedgerEntry::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportDto.SalesLedgerReport.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .entries(entries)
                .grandTotalAmount(grandTotal)
                .grandNetAmount(grandNet)
                .totalInvoices(entries.size())
                .build();
    }

    // 3. PURCHASE LEDGER
    public ReportDto.PurchaseLedgerReport getPurchaseLedger(LocalDate fromDate, LocalDate toDate) {
        List<Purchase> purchases = purchaseRepository.findByBillDateBetween(fromDate, toDate);

        List<ReportDto.PurchaseLedgerEntry> entries = purchases.stream()
                .map(p -> ReportDto.PurchaseLedgerEntry.builder()
                        .purchaseDate(p.getBillDate())
                        .billNo(p.getBillNo())
                        .supplierName(p.getSupplier().getSupplierName())
                        .totalAmount(p.getTotalAmount())
                        .taxAmount(p.getTaxAmount())
                        .netAmount(p.getNetAmount())
                        .build())
                .collect(Collectors.toList());

        BigDecimal grandTotal = entries.stream().map(ReportDto.PurchaseLedgerEntry::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal grandNet = entries.stream().map(ReportDto.PurchaseLedgerEntry::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportDto.PurchaseLedgerReport.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .entries(entries)
                .grandTotalAmount(grandTotal)
                .grandNetAmount(grandNet)
                .totalPurchases(entries.size())
                .build();
    }

    // 4. STOCK LEDGER
    public ReportDto.StockLedgerReport getStockLedger(Long itemId, LocalDate fromDate, LocalDate toDate) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemId));

        List<ReportDto.StockLedgerEntry> entries = new ArrayList<>();
        BigDecimal balance = item.getOpeningStock() != null ? item.getOpeningStock() : BigDecimal.ZERO;

        entries.add(ReportDto.StockLedgerEntry.builder()
                .date(fromDate)
                .reference("Opening Stock")
                .inQty(balance)
                .outQty(BigDecimal.ZERO)
                .balance(balance)
                .build());

        List<Billing> billings = billingRepository.findByBillDateBetween(fromDate, toDate);
        List<Purchase> purchases = purchaseRepository.findByBillDateBetween(fromDate, toDate);

        for (Purchase p : purchases) {
            for (PurchaseDetail pd : p.getPurchaseDetails()) {
                if (pd.getItem().getItemId().equals(itemId)) {
                    entries.add(ReportDto.StockLedgerEntry.builder()
                            .date(p.getBillDate())
                            .reference("PUR: " + p.getBillNo())
                            .inQty(pd.getQuantity())
                            .outQty(BigDecimal.ZERO)
                            .balance(BigDecimal.ZERO)
                            .build());
                }
            }
        }

        for (Billing b : billings) {
            for (BillingDetail bd : b.getBillingDetails()) {
                if (bd.getItem().getItemId().equals(itemId)) {
                    entries.add(ReportDto.StockLedgerEntry.builder()
                            .date(b.getBillDate())
                            .reference("SAL: " + b.getBillNo())
                            .inQty(BigDecimal.ZERO)
                            .outQty(bd.getQuantity())
                            .balance(BigDecimal.ZERO)
                            .build());
                }
            }
        }

        // Must sort stably so Opening Stock stays first if dates are same. Since Opening Stock is at fromDate,
        // it will naturally be at or before other entries.
        entries.sort(Comparator.comparing(ReportDto.StockLedgerEntry::getDate));
        
        BigDecimal runningBalance = BigDecimal.ZERO;
        for (int i = 0; i < entries.size(); i++) {
            ReportDto.StockLedgerEntry e = entries.get(i);
            if (i == 0) {
                runningBalance = e.getInQty(); // opening stock
            } else {
                runningBalance = runningBalance.add(e.getInQty()).subtract(e.getOutQty());
            }
            e.setBalance(runningBalance);
        }

        return ReportDto.StockLedgerReport.builder()
                .itemId(item.getItemId())
                .itemCode(item.getItemCode())
                .itemName(item.getItemName())
                .unit(item.getUnitOfMeasure())
                .fromDate(fromDate)
                .toDate(toDate)
                .entries(entries)
                .closingStock(runningBalance)
                .build();
    }

    // 5. SALES TAX LEDGER
    public ReportDto.SalesTaxLedgerReport getSalesTaxLedger(LocalDate fromDate, LocalDate toDate) {
        List<Billing> billings = billingRepository.findByBillDateBetween(fromDate, toDate);

        List<ReportDto.SalesTaxLedgerEntry> entries = billings.stream()
                .map(b -> ReportDto.SalesTaxLedgerEntry.builder()
                        .date(b.getBillDate())
                        .billNo(b.getBillNo())
                        .customerName(b.getCustomer().getCustomerName())
                        .taxableAmount(b.getTotalAmount().subtract(b.getDiscountAmount() != null ? b.getDiscountAmount() : BigDecimal.ZERO))
                        .taxAmount(b.getTaxAmount())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalTaxable = entries.stream().map(ReportDto.SalesTaxLedgerEntry::getTaxableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = entries.stream().map(ReportDto.SalesTaxLedgerEntry::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportDto.SalesTaxLedgerReport.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .entries(entries)
                .totalTaxableAmount(totalTaxable)
                .totalTaxCollected(totalTax)
                .build();
    }

    // 6. TRIAL BALANCE
    public ReportDto.TrialBalanceReport getTrialBalance(LocalDate asOfDate) {
        List<Customer> customers = customerRepository.findAll();
        List<ReportDto.TrialBalanceEntry> entries = new ArrayList<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (Customer customer : customers) {
            BigDecimal totalSales = billingRepository.findByCustomer_CustomerId(customer.getCustomerId())
                    .stream()
                    .filter(b -> !b.getBillDate().isAfter(asOfDate))
                    .map(Billing::getNetAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalReceived = receiptRepository.findByCustomer_CustomerId(customer.getCustomerId())
                    .stream()
                    .filter(r -> !r.getReceiptDate().isAfter(asOfDate))
                    .map(Receipt::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal opening = customer.getOpeningBalance() != null ? customer.getOpeningBalance() : BigDecimal.ZERO;
            BigDecimal closing = opening.add(totalSales).subtract(totalReceived);

            entries.add(ReportDto.TrialBalanceEntry.builder()
                    .customerCode(customer.getCustomerCode())
                    .customerName(customer.getCustomerName())
                    .openingBalance(opening)
                    .totalSales(totalSales)
                    .totalReceived(totalReceived)
                    .closingBalance(closing)
                    .build());

            if (closing.compareTo(BigDecimal.ZERO) > 0) {
                totalDebit = totalDebit.add(closing);
            } else {
                totalCredit = totalCredit.add(closing.abs());
            }
        }

        return ReportDto.TrialBalanceReport.builder()
                .asOfDate(asOfDate)
                .entries(entries)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .netBalance(totalDebit.subtract(totalCredit))
                .build();
    }

    // 7. PROFIT & LOSS STATEMENT
    public ReportDto.ProfitLossReport getProfitLossStatement(LocalDate fromDate, LocalDate toDate) {
        List<Billing> billings = billingRepository.findByBillDateBetween(fromDate, toDate);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCogs = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Billing billing : billings) {
            totalRevenue = totalRevenue.add(billing.getNetAmount());
            totalDiscount = totalDiscount.add(billing.getDiscountAmount() != null ? billing.getDiscountAmount() : BigDecimal.ZERO);

            for (BillingDetail detail : billing.getBillingDetails()) {
                BigDecimal purchaseRate = detail.getItem().getPurchaseRate() != null ? detail.getItem().getPurchaseRate() : BigDecimal.ZERO;
                totalCogs = totalCogs.add(detail.getQuantity().multiply(purchaseRate));
            }
        }

        BigDecimal grossProfit = totalRevenue.subtract(totalCogs);
        BigDecimal grossProfitPercent = totalRevenue.compareTo(BigDecimal.ZERO) != 0
                ? grossProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ReportDto.ProfitLossReport.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_UP))
                .totalCogs(totalCogs.setScale(2, RoundingMode.HALF_UP))
                .grossProfit(grossProfit.setScale(2, RoundingMode.HALF_UP))
                .grossProfitPercent(grossProfitPercent)
                .totalInvoices(billings.size())
                .totalDiscount(totalDiscount.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
