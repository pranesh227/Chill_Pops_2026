package com.chillpops.service;

import com.chillpops.dto.ProductDtos.*;
import com.chillpops.entity.Category;
import com.chillpops.entity.Product;
import com.chillpops.exception.BusinessException;
import com.chillpops.repository.CategoryRepository;
import com.chillpops.repository.ProductRepository;
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
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    public static final BigDecimal MIN_PRICE = new BigDecimal("10.00");
    public static final BigDecimal MAX_PRICE = new BigDecimal("50.00");

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    @Value("${chillpops.upload.dir:uploads}")
    private String uploadDir;

    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Long categoryId, String search, boolean includeUnavailable) {
        return productRepo.searchProducts(categoryId, search, !includeUnavailable)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product p = productRepo.findById(id).filter(x -> !x.isDeleted())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        return mapToResponse(p);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest req) {
        validatePrice(req.price());
        Category category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Category not found"));

        Product p = new Product();
        p.setName(req.name().trim());
        p.setDescription(req.description() != null ? req.description().trim() : "");
        p.setCategory(category);
        p.setPrice(req.price());
        p.setImagePath(req.imagePath() != null && !req.imagePath().isBlank() ? req.imagePath() : "/images/products/pop_mango.jpg");
        p.setAvailable(req.available() == null || req.available());
        p.setStock(req.stock() != null ? req.stock() : 100);
        p.setDeleted(false);

        return mapToResponse(productRepo.save(p));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest req) {
        validatePrice(req.price());
        Product p = productRepo.findById(id).filter(x -> !x.isDeleted())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));

        Category category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Category not found"));

        p.setName(req.name().trim());
        p.setDescription(req.description() != null ? req.description().trim() : "");
        p.setCategory(category);
        p.setPrice(req.price());
        if (req.imagePath() != null && !req.imagePath().isBlank()) {
            p.setImagePath(req.imagePath());
        }
        if (req.available() != null) {
            p.setAvailable(req.available());
        }
        if (req.stock() != null) {
            p.setStock(req.stock());
        }

        return mapToResponse(productRepo.save(p));
    }

    @Transactional
    public ProductResponse toggleAvailability(Long id) {
        Product p = productRepo.findById(id).filter(x -> !x.isDeleted())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        p.setAvailable(!p.isAvailable());
        return mapToResponse(productRepo.save(p));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product p = productRepo.findById(id).filter(x -> !x.isDeleted())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        p.setDeleted(true);
        productRepo.save(p);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepo.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot upload empty file");
        }
        try {
            Path targetDir = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = "pop_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path filePath = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store image: " + e.getMessage());
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(MIN_PRICE) < 0 || price.compareTo(MAX_PRICE) > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Invalid product price! Stick ice creams must be priced between ₹10 and ₹50. Provided: ₹" + price);
        }
    }

    public ProductResponse mapToResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getPrice(),
                p.getImagePath(),
                p.isAvailable(),
                p.getStock(),
                p.isDeleted()
        );
    }
}
