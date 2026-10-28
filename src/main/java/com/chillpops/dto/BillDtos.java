package com.chillpops.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class BillDtos {

    public record BillItemRequest(
            @NotNull(message = "Product ID is required") Long productId,
            @NotNull(message = "Quantity is required") Integer quantity
    ) {}

    public record CreateBill(
            @NotNull(message = "Idempotency key is required") String idempotencyKey,
            @NotEmpty(message = "At least one item is required") List<BillItemRequest> items
    ) {}

    public record ConfirmPayment(
            @NotNull(message = "Payment method is required") String method,
            String upiReference
    ) {}

    public record BillItemResponse(
            Long id,
            Long productId,
            String productName,
            String categoryName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal lineTotal
    ) {}

    public record PaymentDetailResponse(
            String method,
            String upiReference,
            String confirmedBy,
            String confirmedAt
    ) {}

    public record BillResponse(
            Long id,
            String invoiceNumber,
            String idempotencyKey,
            String status,
            BigDecimal subtotal,
            BigDecimal taxRate,
            BigDecimal taxAmount,
            BigDecimal grandTotal,
            boolean gstApplied,
            Instant createdAt,
            Instant completedAt,
            List<BillItemResponse> items,
            PaymentDetailResponse paymentDetail
    ) {}
}
