package com.chillpops.repository;

import com.chillpops.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByDeletedFalseOrderByIdAsc();

    List<Product> findByDeletedFalseAndAvailableTrueOrderByIdAsc();

    @Query("SELECT p FROM Product p WHERE p.deleted = false " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:availableOnly = false OR p.available = true) " +
           "ORDER BY p.id ASC")
    List<Product> searchProducts(@Param("categoryId") Long categoryId,
                                @Param("search") String search,
                                @Param("availableOnly") boolean availableOnly);
}
