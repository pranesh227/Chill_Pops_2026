package com.chillpops.controller;

import com.chillpops.dto.ReportDto.DashboardStatsResponse;
import com.chillpops.dto.ReportDto.ReportResponse;
import com.chillpops.service.ReportExportService;
import com.chillpops.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportExportService exportService;
    private final ZoneId zoneId;

    public ReportController(ReportService reportService, ReportExportService exportService, ZoneId zoneId) {
        this.reportService = reportService;
        this.exportService = exportService;
        this.zoneId = zoneId;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboard() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }

    @GetMapping("/today")
    public ResponseEntity<ReportResponse> getToday() {
        return ResponseEntity.ok(reportService.getTodayReport());
    }

    @GetMapping("/yesterday")
    public ResponseEntity<ReportResponse> getYesterday() {
        return ResponseEntity.ok(reportService.getYesterdayReport());
    }

    @GetMapping("/month")
    public ResponseEntity<ReportResponse> getMonth(@RequestParam(required = false) String month) {
        YearMonth ym = (month != null && !month.isBlank()) ? YearMonth.parse(month) : YearMonth.now(zoneId);
        return ResponseEntity.ok(reportService.getMonthReport(ym));
    }

    @GetMapping("/range")
    public ResponseEntity<ReportResponse> getRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.getRangeReport(from, to));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws Exception {

        LocalDate s = from != null ? from : LocalDate.now(zoneId).withDayOfMonth(1);
        LocalDate e = to != null ? to : LocalDate.now(zoneId);
        ReportResponse report = reportService.getRangeReport(s, e);
        byte[] excelBytes = exportService.exportToExcel(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "chillpops-sales-" + s + "-to-" + e + ".xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws Exception {

        LocalDate s = from != null ? from : LocalDate.now(zoneId).withDayOfMonth(1);
        LocalDate e = to != null ? to : LocalDate.now(zoneId);
        ReportResponse report = reportService.getRangeReport(s, e);
        byte[] pdfBytes = exportService.exportToPdf(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "chillpops-sales-" + s + "-to-" + e + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
