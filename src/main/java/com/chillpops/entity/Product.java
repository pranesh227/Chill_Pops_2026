package com.chillpops.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @DecimalMin(value = "10.00", message = "Minimum ice cream price is ₹10.00")
    @DecimalMax(value = "50.00", message = "Maximum ice cream price is ₹50.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_path")
    private String imagePath;

    @Column(nullable = false)
    private boolean available = true;

    private Integer stock = 100;

    @Column(nullable = false)
    private boolean deleted = false;
}
