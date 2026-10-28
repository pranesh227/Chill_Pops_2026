package com.chillpops.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SettingsDto {

    public record ShopSettingRequest(
            @NotBlank(message = "Shop name cannot be empty") String shopName,
            @NotBlank(message = "UPI ID cannot be empty") String upiId,
            String upiAccountName,
            String qrImagePath,
            @NotNull(message = "Tax rate is required")
            @DecimalMin(value = "0.00", message = "Tax rate cannot be negative")
            @DecimalMax(value = "100.00", message = "Tax rate cannot exceed 100%")
            BigDecimal taxRate,
            @NotNull(message = "GST enabled flag is required")
            Boolean gstEnabled,
            String shopAddress,
            String contactNumber,
            String logoPath,
            String invoiceFooter
    ) {}

    public record ShopSettingResponse(
            Short id,
            String shopName,
            String upiId,
            String upiAccountName,
            String qrImagePath,
            BigDecimal taxRate,
            boolean gstEnabled,
            String shopAddress,
            String contactNumber,
            String logoPath,
            String invoiceFooter
    ) {}
}
