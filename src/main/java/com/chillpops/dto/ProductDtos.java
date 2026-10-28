package com.chillpops.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductDtos {

    public record ProductRequest(
            @NotBlank(message = "Flavour name is required") String name,
            String description,
            @NotNull(message = "Category ID is required") Long categoryId,
            @NotNull(message = "Price is required")
            @DecimalMin(value = "10.00", message = "Price must be at least ₹10.00")
            @DecimalMax(value = "50.00", message = "Price cannot exceed ₹50.00")
            BigDecimal price,
            String imagePath,
            Boolean available,
            Integer stock
    ) {}

    public record ProductResponse(
            Long id,
            String name,
            String description,
            Long categoryId,
            String categoryName,
            BigDecimal price,
            String imagePath,
            boolean available,
            Integer stock,
            boolean deleted
    ) {}

    public record CategoryResponse(
            Long id,
            String name
    ) {}
}
