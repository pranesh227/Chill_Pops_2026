package com.chillpops.controller;

import com.chillpops.dto.BillDtos.BillResponse;
import com.chillpops.entity.Tx;
import com.chillpops.entity.TxStatus;
import com.chillpops.exception.BusinessException;
import com.chillpops.repository.TxRepository;
import com.chillpops.service.BillingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TxRepository txRepo;
    private final BillingService billingService;
    private final ZoneId zoneId;

    public TransactionController(TxRepository txRepo, BillingService billingService, ZoneId zoneId) {
        this.txRepo = txRepo;
        this.billingService = billingService;
        this.zoneId = zoneId;
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> searchTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        TxStatus txStatus = null;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            try {
                txStatus = TxStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
            }
        }

        Instant start = from != null ? from.atStartOfDay(zoneId).toInstant() : null;
        Instant end = to != null ? to.plusDays(1).atStartOfDay(zoneId).toInstant() : null;

        List<BillResponse> list = txRepo.searchTransactions(txStatus, search, start, end)
                .stream()
                .map(billingService::mapToResponse)
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<BillResponse>> getRecent() {
        List<BillResponse> list = txRepo.findRecentTransactions(PageRequest.of(0, 20))
                .stream()
                .map(billingService::mapToResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getById(@PathVariable Long id) {
        Tx tx = txRepo.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id));
        return ResponseEntity.ok(billingService.mapToResponse(tx));
    }
}
