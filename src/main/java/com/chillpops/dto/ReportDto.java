package com.chillpops.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportDto {

    public record DashboardStatsResponse(
            long totalProducts,
            long totalOrders,
            BigDecimal todaySales,
            BigDecimal monthlyRevenue,
            long totalQuantitySold,
            String bestSellingFlavour,
            List<BillDtos.BillResponse> recentTransactions
    ) {}

    public record ReportResponse(
            LocalDate from,
            LocalDate toInclusive,
            long transactions,
            long itemsSold,
            BigDecimal revenue,
            BigDecimal pending,
            String bestSeller,
            Map<String, BigDecimal> byCategory,
            Map<String, BigDecimal> daily,
            Map<String, BigDecimal> byPaymentMethod,
            Map<String, Long> byProduct
    ) {}
}
