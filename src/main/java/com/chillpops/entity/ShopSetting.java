package com.chillpops.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "shop_settings")
@Getter
@Setter
@NoArgsConstructor
public class ShopSetting {

    @Id
    private Short id = 1;

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName = "Chill Pops";

    @Column(name = "upi_id", length = 80)
    private String upiId = "chillpops@oksbi";

    @Column(name = "upi_account_name", length = 100)
    private String upiAccountName = "Chill Pops Franchise";

    @Column(name = "qr_image_path")
    private String qrImagePath;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("5.00");

    @Column(name = "gst_enabled", nullable = false)
    private boolean gstEnabled = false;

    @Column(name = "shop_address")
    private String shopAddress = "123 Popsicle Street, Chill Zone, Bengaluru, Karnataka 560001";

    @Column(name = "contact_number", length = 30)
    private String contactNumber = "+91 98765 43210";

    @Column(name = "logo_path")
    private String logoPath = "/images/logo.svg";

    @Column(name = "invoice_footer")
    private String invoiceFooter = "Thank you for choosing Chill Pops! Cool your day the Chill Pops way!";
}
