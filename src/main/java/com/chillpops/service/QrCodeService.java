package com.chillpops.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    public byte[] generateUpiQrCode(String upiId, String payeeName, String amount, String invoiceNumber, int size) throws Exception {
        String cleanPayee = (payeeName != null && !payeeName.isBlank()) ? payeeName : "Chill Pops";
        String encodedPayee = URLEncoder.encode(cleanPayee, StandardCharsets.UTF_8).replace("+", "%20");
        String note = URLEncoder.encode("Order " + invoiceNumber, StandardCharsets.UTF_8).replace("+", "%20");

        String encodedUpiId = URLEncoder.encode(upiId, StandardCharsets.UTF_8);

String upiPayload = String.format(
        "upi://pay?pa=%s&pn=%s&am=%s&tr=%s&tn=%s&cu=INR",
        encodedUpiId, encodedPayee, amount, invoiceNumber, note
);

        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = writer.encode(upiPayload, BarcodeFormat.QR_CODE, size, size, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }
}
