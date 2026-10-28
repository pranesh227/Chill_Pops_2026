package com.chillpops.service;

import com.chillpops.dto.SettingsDto.*;
import com.chillpops.entity.ShopSetting;
import com.chillpops.exception.BusinessException;
import com.chillpops.repository.ShopSettingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class SettingsService {

    private final ShopSettingRepository settingRepo;

    @Value("${chillpops.upload.dir:uploads}")
    private String uploadDir;

    public SettingsService(ShopSettingRepository settingRepo) {
        this.settingRepo = settingRepo;
    }

    @Transactional(readOnly = true)
    public ShopSettingResponse getSettings() {
        ShopSetting setting = settingRepo.findById((short) 1)
                .orElseGet(() -> {
                    ShopSetting s = new ShopSetting();
                    s.setId((short) 1);
                    return settingRepo.save(s);
                });
        return mapToResponse(setting);
    }

    @Transactional
    public ShopSettingResponse updateSettings(ShopSettingRequest req) {
        ShopSetting setting = settingRepo.findById((short) 1)
                .orElseGet(() -> {
                    ShopSetting s = new ShopSetting();
                    s.setId((short) 1);
                    return s;
                });

        setting.setShopName(req.shopName().trim());
        setting.setUpiId(req.upiId().trim());
        setting.setUpiAccountName(req.upiAccountName() != null ? req.upiAccountName().trim() : "Chill Pops Franchise");
        setting.setTaxRate(req.taxRate() != null ? req.taxRate() : BigDecimal.ZERO);
        setting.setGstEnabled(req.gstEnabled() != null && req.gstEnabled());

        if (req.shopAddress() != null) setting.setShopAddress(req.shopAddress().trim());
        if (req.contactNumber() != null) setting.setContactNumber(req.contactNumber().trim());
        if (req.logoPath() != null) setting.setLogoPath(req.logoPath().trim());
        if (req.invoiceFooter() != null) setting.setInvoiceFooter(req.invoiceFooter().trim());
        if (req.qrImagePath() != null && !req.qrImagePath().isBlank()) {
            setting.setQrImagePath(req.qrImagePath().trim());
        }

        ShopSetting saved = settingRepo.saveAndFlush(setting);
        return mapToResponse(saved);
    }

    public String uploadQrCode(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot upload empty QR code image");
        }
        try {
            Path targetDir = Paths.get(uploadDir).toAbsolutePath();
            System.out.println("UPLOAD DIRECTORY: " + targetDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = "qr_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path filePath = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("QR FILE EXISTS: " + Files.exists(filePath));
System.out.println("QR FILE PATH: " + filePath);
            String path = "/uploads/" + fileName;

            // Also update the shop setting automatically
            ShopSetting setting = settingRepo.findById((short) 1).orElseGet(ShopSetting::new);
            setting.setQrImagePath(path);
            settingRepo.save(setting);

            return path;
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload QR code: " + e.getMessage());
        }
    }

    private ShopSettingResponse mapToResponse(ShopSetting s) {
        return new ShopSettingResponse(
                s.getId(),
                s.getShopName(),
                s.getUpiId(),
                s.getUpiAccountName(),
                s.getQrImagePath(),
                s.getTaxRate(),
                s.isGstEnabled(),
                s.getShopAddress(),
                s.getContactNumber(),
                s.getLogoPath(),
                s.getInvoiceFooter()
        );
    }
}
