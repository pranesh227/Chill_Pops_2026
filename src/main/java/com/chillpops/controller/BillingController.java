package com.chillpops.controller;

import com.chillpops.dto.BillDtos.*;
import com.chillpops.dto.SettingsDto.ShopSettingResponse;
import com.chillpops.service.BillingService;
import com.chillpops.service.PdfInvoiceService;
import com.chillpops.service.QrCodeService;
import com.chillpops.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;
    private final SettingsService settingsService;
    private final QrCodeService qrCodeService;
    private final PdfInvoiceService pdfInvoiceService;

    public BillingController(BillingService billingService,
                             SettingsService settingsService,
                             QrCodeService qrCodeService,
                             PdfInvoiceService pdfInvoiceService) {
        this.billingService = billingService;
        this.settingsService = settingsService;
        this.qrCodeService = qrCodeService;
        this.pdfInvoiceService = pdfInvoiceService;
    }

    @PostMapping("/create")
    public ResponseEntity<BillResponse> createBill(@Valid @RequestBody CreateBill req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createBill(req));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BillResponse> confirmPayment(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmPayment req,
            Principal principal) {
        String username = principal != null ? principal.getName() : "Owner";
        return ResponseEntity.ok(billingService.confirmPayment(id, req, username));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BillResponse> cancelBill(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.cancelBill(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getBill(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBill(id));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<BillResponse> getBillByInvoiceNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(billingService.getBillByInvoiceNumber(invoiceNumber));
    }

    @GetMapping(value = "/qr/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getUpiQrCode(@PathVariable Long id) throws Exception {
        BillResponse bill = billingService.getBill(id);
        ShopSettingResponse settings = settingsService.getSettings();

        byte[] qrBytes = qrCodeService.generateUpiQrCode(
                settings.upiId(),
                settings.upiAccountName() != null ? settings.upiAccountName() : settings.shopName(),
                bill.grandTotal().toString(),
                bill.invoiceNumber(),
                300
        );

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrBytes);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id) throws Exception {
        BillResponse bill = billingService.getBill(id);
        ShopSettingResponse settings = settingsService.getSettings();

        byte[] pdfBytes = pdfInvoiceService.generateInvoicePdf(bill, settings);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", bill.invoiceNumber() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
