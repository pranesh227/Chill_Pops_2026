package com.chillpops.config;

import com.chillpops.entity.Category;
import com.chillpops.entity.Product;
import com.chillpops.entity.ShopSetting;
import com.chillpops.entity.User;
import com.chillpops.repository.CategoryRepository;
import com.chillpops.repository.ProductRepository;
import com.chillpops.repository.ShopSettingRepository;
import com.chillpops.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final ShopSettingRepository settingRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepo,
                           CategoryRepository categoryRepo,
                           ProductRepository productRepo,
                           ShopSettingRepository settingRepo,
                           PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.settingRepo = settingRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Ensure Default Admin User: admin / admin123
        if (userRepo.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("OWNER");
            admin.setEnabled(true);
            userRepo.save(admin);
        }

        // 2. Ensure Default Shop Settings
        if (settingRepo.findById((short) 1).isEmpty()) {
            ShopSetting setting = new ShopSetting();
            setting.setId((short) 1);
            setting.setShopName("Chill Pops");
            setting.setUpiId("chillpops@oksbi");
            setting.setUpiAccountName("Chill Pops Franchise");
            setting.setTaxRate(new BigDecimal("5.00"));
            setting.setGstEnabled(false); // GST disabled by default per user requirement!
            setting.setShopAddress("123 Popsicle Street, Chill Zone, Bengaluru, Karnataka 560001");
            setting.setContactNumber("+91 98765 43210");
            setting.setLogoPath("/images/logo.svg");
            setting.setInvoiceFooter("Thank you for choosing Chill Pops! Cool your day the Chill Pops way!");
            settingRepo.save(setting);
        }

        // 3. Ensure Stick Ice Cream Categories
        if (categoryRepo.count() == 0) {
            categoryRepo.saveAll(List.of(
                    new Category("Fruit Pops"),
                    new Category("Choco Sticks"),
                    new Category("Cream Sticks"),
                    new Category("Exotic & Premium Sticks")
            ));
        }

        // 4. Ensure Products (if empty)
        if (productRepo.count() == 0) {
            Category fruitPops = categoryRepo.findByNameIgnoreCase("Fruit Pops").orElseGet(() -> categoryRepo.save(new Category("Fruit Pops")));
            Category chocoSticks = categoryRepo.findByNameIgnoreCase("Choco Sticks").orElseGet(() -> categoryRepo.save(new Category("Choco Sticks")));
            Category creamSticks = categoryRepo.findByNameIgnoreCase("Cream Sticks").orElseGet(() -> categoryRepo.save(new Category("Cream Sticks")));
            Category premiumSticks = categoryRepo.findByNameIgnoreCase("Exotic & Premium Sticks").orElseGet(() -> categoryRepo.save(new Category("Exotic & Premium Sticks")));

            List<Product> defaults = List.of(
                createProduct("Classic Mango Pop", "Juicy natural Alphonso mango popsicle with a cool tropical blast.", fruitPops, "10.00", "/images/products/pop_mango.jpg"),
                createProduct("Orange Splash Pop", "Refreshing zesty sun-kissed orange citrus stick ice cream.", fruitPops, "10.00", "/images/products/pop_orange.jpg"),
                createProduct("Strawberry Chill Pop", "Sweet farm-fresh real strawberry fruit popsicle with berry chunks.", fruitPops, "20.00", "/images/products/pop_strawberry.jpg"),
                createProduct("Pineapple Twist Pop", "Tangy sweet tropical pineapple ice bar with cool mint undertones.", fruitPops, "20.00", "/images/products/pop_pineapple.jpg"),
                createProduct("Rose Milk Pop", "Aromatic fragrant rose milk cream stick infused with cardamom.", creamSticks, "20.00", "/images/products/pop_rosemilk.jpg"),
                createProduct("Watermelon Breeze Pop", "Crisp hydrating summer watermelon popsicle with a lime twist.", fruitPops, "20.00", "/images/products/pop_watermelon.jpg"),
                createProduct("Tangy Kala Khatta Stick", "Desi style spiced sweet and tangy blackberry ice pop stick.", fruitPops, "15.00", "/images/products/pop_kalakhatta.jpg"),
                createProduct("Blueberry Blast Pop", "Wild mountain blueberry popsicle bursting with rich antioxidant flavor.", fruitPops, "30.00", "/images/products/pop_blueberry.jpg"),
                createProduct("Chocolate Crunch Stick", "Velvety milk chocolate ice stick coated in crispy waffle crunch.", chocoSticks, "30.00", "/images/products/pop_chocolate.jpg"),
                createProduct("Blackcurrant Freeze Pop", "Deep purple sweet and sour blackcurrant stick for an icy kick.", fruitPops, "30.00", "/images/products/pop_blackcurrant.jpg"),
                createProduct("Butterscotch Delight Stick", "Rich caramelized butterscotch ice cream stick with crunchy praline nuts.", creamSticks, "40.00", "/images/products/pop_butterscotch.jpg"),
                createProduct("Belgian Chocolate Stick", "Decadent 70% dark Belgian cocoa stick ice cream with a glossy fudge dip.", chocoSticks, "40.00", "/images/products/pop_belgian_choc.jpg"),
                createProduct("Mixed Berry Blast Pop", "A trio of raspberry, blueberry, and cranberry in a frosted stick.", fruitPops, "40.00", "/images/products/pop_mixed_berry.jpg"),
                createProduct("Malai Kulfi Stick", "Authentic slow-simmered rich creamy rabdi kulfi stick on traditional wood.", creamSticks, "35.00", "/images/products/pop_malai_kulfi.jpg"),
                createProduct("Roasted Almond Stick", "Nutty roasted California almond bits studded over silky cream stick.", premiumSticks, "45.00", "/images/products/pop_roasted_almond.jpg"),
                createProduct("Premium Mango Cream Stick", "Luscious Alphonso mango pulp blended with fresh dairy cream on a stick.", premiumSticks, "50.00", "/images/products/pop_mango_cream.jpg"),
                createProduct("Double Chocolate Chill Stick", "Double layered intense dark and milk chocolate gelato stick.", chocoSticks, "50.00", "/images/products/pop_double_choc.jpg"),
                createProduct("Kesar Badam Stick", "Royal saffron threads and crushed Kashmiri badam in frozen cream.", premiumSticks, "50.00", "/images/products/pop_kesar_badam.jpg")
            );

            productRepo.saveAll(defaults);
        }
    }

    private Product createProduct(String name, String desc, Category category, String price, String img) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setCategory(category);
        p.setPrice(new BigDecimal(price));
        p.setImagePath(img);
        p.setAvailable(true);
        p.setStock(100);
        p.setDeleted(false);
        return p;
    }
}
