package com.chillpops.service;

import com.chillpops.dto.BillDtos.BillResponse;
import com.chillpops.dto.ReportDto.DashboardStatsResponse;
import com.chillpops.dto.ReportDto.ReportResponse;
import com.chillpops.entity.Tx;
import com.chillpops.entity.TxItem;
import com.chillpops.entity.TxStatus;
import com.chillpops.repository.ProductRepository;
import com.chillpops.repository.TxRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TxRepository txRepo;
    private final ProductRepository productRepo;
    private final BillingService billingService;
    private final JdbcTemplate jdbc;
    private final ZoneId zone;

    public ReportService(TxRepository txRepo,
                         ProductRepository productRepo,
                         BillingService billingService,
                         JdbcTemplate jdbc,
                         ZoneId zone) {
        this.txRepo = txRepo;
        this.productRepo = productRepo;
        this.billingService = billingService;
        this.jdbc = jdbc;
        this.zone = zone;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        LocalDate today = LocalDate.now(zone);
        Instant todayStart = today.atStartOfDay(zone).toInstant();
        Instant todayEnd = today.plusDays(1).atStartOfDay(zone).toInstant();

        YearMonth currentMonth = YearMonth.now(zone);
        Instant monthStart = currentMonth.atDay(1).atStartOfDay(zone).toInstant();
        Instant monthEnd = currentMonth.atEndOfMonth().plusDays(1).atStartOfDay(zone).toInstant();

        long totalProducts = productRepo.findByDeletedFalseOrderByIdAsc().size();
        long totalOrders = txRepo.countByStatus(TxStatus.COMPLETED);

        List<Tx> todayTxs = txRepo.findCompleted(todayStart, todayEnd);
        BigDecimal todaySales = todayTxs.stream()
                .map(Tx::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Tx> monthTxs = txRepo.findCompleted(monthStart, monthEnd);
        BigDecimal monthlyRevenue = monthTxs.stream()
                .map(Tx::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalQuantitySold = txRepo.findAll().stream()
                .filter(t -> t.getStatus() == TxStatus.COMPLETED)
                .flatMap(t -> t.getItems().stream())
                .mapToLong(TxItem::getQuantity)
                .sum();

        String bestSeller = txRepo.findAll().stream()
                .filter(t -> t.getStatus() == TxStatus.COMPLETED)
                .flatMap(t -> t.getItems().stream())
                .collect(Collectors.groupingBy(TxItem::getProductName, Collectors.summingInt(TxItem::getQuantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("—");

        List<BillResponse> recent = txRepo.findRecentTransactions(PageRequest.of(0, 10))
                .stream()
                .map(billingService::mapToResponse)
                .toList();

        return new DashboardStatsResponse(
                totalProducts,
                totalOrders,
                todaySales,
                monthlyRevenue,
                totalQuantitySold,
                bestSeller,
                recent
        );
    }

    @Transactional(readOnly = true)
    public ReportResponse getTodayReport() {
        LocalDate today = LocalDate.now(zone);
        return getRangeReport(today, today);
    }

    @Transactional(readOnly = true)
    public ReportResponse getYesterdayReport() {
        LocalDate yesterday = LocalDate.now(zone).minusDays(1);
        return getRangeReport(yesterday, yesterday);
    }

    @Transactional(readOnly = true)
    public ReportResponse getMonthReport(YearMonth ym) {
        return getRangeReport(ym.atDay(1), ym.atEndOfMonth());
    }

    @Transactional(readOnly = true)
    public ReportResponse getRangeReport(LocalDate from, LocalDate toInclusive) {
        if (toInclusive.isBefore(from)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Instant s = from.atStartOfDay(zone).toInstant();
        Instant e = toInclusive.plusDays(1).atStartOfDay(zone).toInstant();

        List<Tx> sales = txRepo.findCompleted(s, e);

        // Daily trend
        Map<String, BigDecimal> daily = new LinkedHashMap<>();
        for (LocalDate d = from; !d.isAfter(toInclusive); d = d.plusDays(1)) {
            daily.put(d.toString(), BigDecimal.ZERO);
        }
        for (Tx t : sales) {
            if (t.getCompletedAt() != null) {
                String dayKey = t.getCompletedAt().atZone(zone).toLocalDate().toString();
                daily.merge(dayKey, t.getGrandTotal(), BigDecimal::add);
            }
        }

        // Category-wise revenue
        var items = sales.stream().flatMap(t -> t.getItems().stream()).toList();
        Map<String, BigDecimal> cat = items.stream().collect(Collectors.groupingBy(
                TxItem::getCategoryName,
                TreeMap::new,
                Collectors.reducing(BigDecimal.ZERO, TxItem::getLineTotal, BigDecimal::add)
        ));

        // Product-wise sales quantity
        Map<String, Long> byProduct = items.stream().collect(Collectors.groupingBy(
                TxItem::getProductName,
                TreeMap::new,
                Collectors.summingLong(TxItem::getQuantity)
        ));

        String best = byProduct.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("—");

        // Payment method breakdown
        Map<String, BigDecimal> byPaymentMethod = new LinkedHashMap<>();
        byPaymentMethod.put("CASH", BigDecimal.ZERO);
        byPaymentMethod.put("UPI", BigDecimal.ZERO);

        for (Tx t : sales) {
            String method = "CASH";
            if (t.getPaymentDetail() != null && t.getPaymentDetail().getMethod() != null) {
                method = t.getPaymentDetail().getMethod().toUpperCase();
            } else {
                try {
                    String m = jdbc.queryForObject("SELECT method FROM payment_details WHERE transaction_id = ?", String.class, t.getId());
                    if (m != null) method = m.toUpperCase();
                } catch (Exception ignored) {}
            }
            byPaymentMethod.merge(method, t.getGrandTotal(), BigDecimal::add);
        }

        BigDecimal revenue = sales.stream()
                .map(Tx::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pending = txRepo.pendingTotal(s, e);
        if (pending == null) pending = BigDecimal.ZERO;

        long itemsSold = items.stream().mapToLong(TxItem::getQuantity).sum();

        return new ReportResponse(
                from,
                toInclusive,
                sales.size(),
                itemsSold,
                revenue,
                pending,
                best,
                cat,
                daily,
                byPaymentMethod,
                byProduct
        );
    }
}
