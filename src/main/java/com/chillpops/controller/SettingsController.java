package com.chillpops.controller;

import com.chillpops.dto.SettingsDto.ShopSettingRequest;
import com.chillpops.dto.SettingsDto.ShopSettingResponse;
import com.chillpops.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<ShopSettingResponse> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    public ResponseEntity<ShopSettingResponse> updateSettings(@Valid @RequestBody ShopSettingRequest req) {
        return ResponseEntity.ok(settingsService.updateSettings(req));
    }

    @PostMapping(value = "/upload-qr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadQrCode(@RequestParam("file") MultipartFile file) {
        String path = settingsService.uploadQrCode(file);
        return ResponseEntity.ok(Map.of("url", path));
    }
}
