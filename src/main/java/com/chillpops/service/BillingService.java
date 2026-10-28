package com.chillpops.service;

import com.chillpops.dto.BillDtos.*;
import com.chillpops.entity.*;
import com.chillpops.exception.BusinessException;
import com.chillpops.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

@Service
public class BillingService {

    private final TxRepository txRepo;
    private final ProductRepository productRepo;
    private final ShopSettingRepository shopSettingRepo;
    private final InvoiceNumberService invoiceNoService;
    private final JdbcTemplate jdbc;
    private final Clock clock;

    public BillingService(TxRepository txRepo,
                          ProductRepository productRepo,
                          ShopSettingRepository shopSettingRepo,
                          InvoiceNumberService invoiceNoService,
                          JdbcTemplate jdbc,
                          Clock clock) {
        this.txRepo = txRepo;
        this.productRepo = productRepo;
        this.shopSettingRepo = shopSettingRepo;
        this.invoiceNoService = invoiceNoService;
        this.jdbc = jdbc;
        this.clock = clock;
    }

    /** Creates a PENDING invoice. Prices come from DB only. Idempotent per key. */
    @Transactional
    public BillResponse createBill(CreateBill req) {
        Optional<Tx> existing = txRepo.findByIdempotencyKey(req.idempotencyKey());
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        ShopSetting setting = shopSettingRepo.findById((short) 1).orElseGet(ShopSetting::new);
        boolean gstEnabled = setting.isGstEnabled();
        BigDecimal taxRate = gstEnabled ? setting.getTaxRate() : BigDecimal.ZERO;

        Tx tx = new Tx();
        tx.setIdempotencyKey(req.idempotencyKey());
        tx.setCreatedAt(clock.instant());
        tx.setStatus(TxStatus.PENDING);

        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (var itemReq : req.items()) {
            if (itemReq.quantity() != null && itemReq.quantity() > 0) {
                merged.merge(itemReq.productId(), itemReq.quantity(), Integer::sum);
            }
        }

        if (merged.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot create bill with empty cart or zero quantities");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (var entry : merged.entrySet()) {
            Product p = productRepo.findById(entry.getKey()).filter(x -> !x.isDeleted())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Unknown product with id: " + entry.getKey()));

            if (!p.isAvailable()) {
                throw new BusinessException(HttpStatus.CONFLICT, p.getName() + " is currently marked as unavailable");
            }

            TxItem item = new TxItem();
            item.setTransaction(tx);
            item.setProductId(p.getId());
            item.setProductName(p.getName());
            item.setCategoryName(p.getCategory().getName());
            item.setUnitPrice(p.getPrice());
            item.setQuantity(entry.getValue());

            BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(entry.getValue())).setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);
            tx.getItems().add(item);
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal taxAmount = BigDecimal.ZERO;
        if (gstEnabled && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        tx.setSubtotal(subtotal);
        tx.setTaxRate(taxRate);
        tx.setTaxAmount(taxAmount);
        tx.setGrandTotal(subtotal.add(taxAmount));
        tx.setInvoiceNumber(invoiceNoService.next());

        try {
            Tx saved = txRepo.saveAndFlush(tx);
            return mapToResponse(saved);
        } catch (DataIntegrityViolationException race) {
            return mapToResponse(txRepo.findByIdempotencyKey(req.idempotencyKey()).orElseThrow(() -> race));
        }
    }

    /** Owner-verified payment confirmation. Row-locked and idempotent. */
    @Transactional
    public BillResponse confirmPayment(Long id, ConfirmPayment req, String ownerUsername) {
        String method = req.method() != null ? req.method().trim().toUpperCase() : "";
        if (!Set.of("UPI", "CASH").contains(method)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Unsupported payment method: " + req.method() + ". Must be CASH or UPI.");
        }

        Tx tx = txRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Invoice not found with id: " + id));

        if (tx.getStatus() == TxStatus.COMPLETED) {
            return mapToResponse(tx);
        }
        if (tx.getStatus() == TxStatus.CANCELLED) {
            throw new BusinessException(HttpStatus.CONFLICT, "Cannot confirm payment. Invoice has been cancelled.");
        }

        Instant now = clock.instant();
        tx.setStatus(TxStatus.COMPLETED);
        tx.setCompletedAt(now);

        String confirmedBy = (ownerUsername != null && !ownerUsername.isBlank()) ? ownerUsername : "Owner";
        String upiRef = (req.upiReference() != null && !req.upiReference().isBlank()) ? req.upiReference().trim() : ("REF-" + System.currentTimeMillis());

        jdbc.update("INSERT INTO payment_details(transaction_id, method, upi_reference, confirmed_by, confirmed_at) VALUES(?,?,?,?,?)",
                id, method, upiRef, confirmedBy, java.sql.Timestamp.from(now));

        return mapToResponse(txRepo.saveAndFlush(tx));
    }

    @Transactional
    public BillResponse cancelBill(Long id) {
        Tx tx = txRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Invoice not found with id: " + id));

        if (tx.getStatus() == TxStatus.COMPLETED) {
            throw new BusinessException(HttpStatus.CONFLICT, "Completed sales cannot be cancelled.");
        }

        tx.setStatus(TxStatus.CANCELLED);
        return mapToResponse(txRepo.saveAndFlush(tx));
    }

    @Transactional(readOnly = true)
    public BillResponse getBill(Long id) {
        Tx tx = txRepo.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Invoice not found with id: " + id));
        return mapToResponse(tx);
    }

    @Transactional(readOnly = true)
    public BillResponse getBillByInvoiceNumber(String invoiceNumber) {
        Tx tx = txRepo.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Invoice not found with number: " + invoiceNumber));
        return mapToResponse(tx);
    }

    public BillResponse mapToResponse(Tx tx) {
        List<BillItemResponse> itemDtos = tx.getItems().stream()
                .map(it -> new BillItemResponse(
                        it.getId(),
                        it.getProductId(),
                        it.getProductName(),
                        it.getCategoryName(),
                        it.getUnitPrice(),
                        it.getQuantity(),
                        it.getLineTotal()
                )).toList();

        PaymentDetailResponse pdr = null;
        if (tx.getPaymentDetail() != null) {
            pdr = new PaymentDetailResponse(
                    tx.getPaymentDetail().getMethod(),
                    tx.getPaymentDetail().getUpiReference(),
                    tx.getPaymentDetail().getConfirmedBy(),
                    tx.getPaymentDetail().getConfirmedAt() != null ? tx.getPaymentDetail().getConfirmedAt().toInstant().toString() : null
            );
        } else if (tx.getStatus() == TxStatus.COMPLETED) {
            // fallback query if relation not refreshed
            try {
                pdr = jdbc.queryForObject("SELECT method, upi_reference, confirmed_by, confirmed_at FROM payment_details WHERE transaction_id = ?",
                        (rs, rowNum) -> new PaymentDetailResponse(
                                rs.getString("method"),
                                rs.getString("upi_reference"),
                                rs.getString("confirmed_by"),
                                rs.getTimestamp("confirmed_at").toInstant().toString()
                        ), tx.getId());
            } catch (Exception ignored) {}
        }

        boolean gstApplied = tx.getTaxAmount() != null && tx.getTaxAmount().compareTo(BigDecimal.ZERO) > 0;

        return new BillResponse(
                tx.getId(),
                tx.getInvoiceNumber(),
                tx.getIdempotencyKey(),
                tx.getStatus().name(),
                tx.getSubtotal(),
                tx.getTaxRate(),
                tx.getTaxAmount(),
                tx.getGrandTotal(),
                gstApplied,
                tx.getCreatedAt(),
                tx.getCompletedAt(),
                itemDtos,
                pdr
        );
    }
}
