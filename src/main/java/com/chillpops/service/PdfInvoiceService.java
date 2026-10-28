package com.chillpops.service;

import com.chillpops.dto.BillDtos.BillResponse;
import com.chillpops.dto.SettingsDto.ShopSettingResponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
    private final ZoneId zoneId;

    public PdfInvoiceService(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public byte[] generateInvoicePdf(BillResponse bill, ShopSettingResponse settings) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Color theme: Executive Brand Crimson (#E11D48), Dark Slate (#0F172A), Light Neutral (#F8FAFC)
        Color brandCrimson = new Color(225, 29, 72);
        Color textDark = new Color(15, 23, 42);
        Color tableHeaderBg = new Color(248, 250, 252);
        Color grayText = new Color(100, 116, 139);

        // Header Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, textDark);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, grayText);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10, textDark);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, textDark);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, brandCrimson);

        Paragraph title = new Paragraph(settings.shopName().toUpperCase(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph tagline = new Paragraph("\"Cool Your Day with the Chill Pops Way!\"", subtitleFont);
        tagline.setAlignment(Element.ALIGN_CENTER);
        tagline.setSpacingAfter(10f);
        document.add(tagline);

        Paragraph shopDetails = new Paragraph(
                (settings.shopAddress() != null ? settings.shopAddress() : "") +
                (settings.contactNumber() != null ? " | Phone: " + settings.contactNumber() : ""), regularFont);
        shopDetails.setAlignment(Element.ALIGN_CENTER);
        shopDetails.setSpacingAfter(15f);
        document.add(shopDetails);

        // Horizontal Separator Line
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderColor(brandCrimson);
        lineCell.setBorderWidth(1.5f);
        lineCell.setPadding(0);
        line.addCell(lineCell);
        line.setSpacingAfter(15f);
        document.add(line);

        // Invoice Meta Info (Invoice No, Date, Status, Payment)
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setWidths(new float[]{1, 1});

        String formattedDate = bill.createdAt() != null ?
                bill.createdAt().atZone(zoneId).format(TIME_FORMATTER) : "N/A";

        PdfPCell leftMeta = new PdfPCell();
        leftMeta.setBorder(Rectangle.NO_BORDER);
        leftMeta.addElement(new Paragraph("INVOICE NUMBER: " + bill.invoiceNumber(), boldFont));
        leftMeta.addElement(new Paragraph("DATE & TIME: " + formattedDate, regularFont));

        PdfPCell rightMeta = new PdfPCell();
        rightMeta.setBorder(Rectangle.NO_BORDER);
        rightMeta.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph statusP = new Paragraph("STATUS: " + bill.status(), boldFont);
        statusP.setAlignment(Element.ALIGN_RIGHT);
        rightMeta.addElement(statusP);

        String paymentInfo = "N/A";
        if (bill.paymentDetail() != null) {
            paymentInfo = bill.paymentDetail().method() + " (Ref: " + bill.paymentDetail().upiReference() + ")";
        }
        Paragraph payP = new Paragraph("PAYMENT METHOD: " + paymentInfo, regularFont);
        payP.setAlignment(Element.ALIGN_RIGHT);
        rightMeta.addElement(payP);

        metaTable.addCell(leftMeta);
        metaTable.addCell(rightMeta);
        metaTable.setSpacingAfter(15f);
        document.add(metaTable);

        // Product Items Table
        PdfPTable itemTable = new PdfPTable(5);
        itemTable.setWidthPercentage(100);
        itemTable.setWidths(new float[]{3.5f, 2.0f, 1.2f, 1.5f, 1.8f});

        addHeaderCell(itemTable, "Stick Ice Cream Flavour", tableHeaderBg, boldFont, Element.ALIGN_LEFT);
        addHeaderCell(itemTable, "Category", tableHeaderBg, boldFont, Element.ALIGN_LEFT);
        addHeaderCell(itemTable, "Qty", tableHeaderBg, boldFont, Element.ALIGN_CENTER);
        addHeaderCell(itemTable, "Price (₹)", tableHeaderBg, boldFont, Element.ALIGN_RIGHT);
        addHeaderCell(itemTable, "Total (₹)", tableHeaderBg, boldFont, Element.ALIGN_RIGHT);

        for (var item : bill.items()) {
            addBodyCell(itemTable, item.productName(), regularFont, Element.ALIGN_LEFT);
            addBodyCell(itemTable, item.categoryName(), regularFont, Element.ALIGN_LEFT);
            addBodyCell(itemTable, String.valueOf(item.quantity()), regularFont, Element.ALIGN_CENTER);
            addBodyCell(itemTable, String.format("%.2f", item.unitPrice()), regularFont, Element.ALIGN_RIGHT);
            addBodyCell(itemTable, String.format("%.2f", item.lineTotal()), regularFont, Element.ALIGN_RIGHT);
        }
        itemTable.setSpacingAfter(15f);
        document.add(itemTable);

        // Totals Section
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(45);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{1.5f, 1f});

        addSummaryRow(totalsTable, "Subtotal:", "₹ " + String.format("%.2f", bill.subtotal()), regularFont, boldFont);

        // Display GST only if enabled/applied
        if (bill.gstApplied() && bill.taxAmount() != null && bill.taxAmount().compareTo(BigDecimal.ZERO) > 0) {
            String gstLabel = "GST (" + bill.taxRate() + "%):";
            addSummaryRow(totalsTable, gstLabel, "₹ " + String.format("%.2f", bill.taxAmount()), regularFont, boldFont);
        }

        PdfPCell grandTotalLabel = new PdfPCell(new Phrase("Grand Total:", totalFont));
        grandTotalLabel.setBorder(Rectangle.TOP);
        grandTotalLabel.setBorderColor(brandCrimson);
        grandTotalLabel.setPadding(6f);

        PdfPCell grandTotalVal = new PdfPCell(new Phrase("₹ " + String.format("%.2f", bill.grandTotal()), totalFont));
        grandTotalVal.setBorder(Rectangle.TOP);
        grandTotalVal.setBorderColor(brandCrimson);
        grandTotalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        grandTotalVal.setPadding(6f);

        totalsTable.addCell(grandTotalLabel);
        totalsTable.addCell(grandTotalVal);
        totalsTable.setSpacingAfter(25f);
        document.add(totalsTable);

        // Footer Message
        Paragraph footer = new Paragraph(
                settings.invoiceFooter() != null ? settings.invoiceFooter() : "Thank you for visiting Chill Pops!", subtitleFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    private void addHeaderCell(PdfPTable table, String text, Color bg, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(align);
        cell.setPadding(6f);
        cell.setBorderColor(new Color(240, 240, 240));
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(6f);
        cell.setBorderColor(new Color(245, 245, 245));
        table.addCell(cell);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font labelFont, Font valFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, labelFont));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(3f);
        PdfPCell c2 = new PdfPCell(new Phrase(value, valFont));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setPadding(3f);
        table.addCell(c1);
        table.addCell(c2);
    }
}
