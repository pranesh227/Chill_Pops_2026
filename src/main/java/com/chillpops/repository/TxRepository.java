package com.chillpops.repository;

import com.chillpops.entity.Tx;
import com.chillpops.entity.TxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TxRepository extends JpaRepository<Tx, Long> {

    Optional<Tx> findByIdempotencyKey(String idempotencyKey);

    Optional<Tx> findByInvoiceNumber(String invoiceNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tx t WHERE t.id = :id")
    Optional<Tx> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT t FROM Tx t WHERE t.status = 'COMPLETED' AND t.completedAt >= :start AND t.completedAt < :end ORDER BY t.completedAt ASC")
    List<Tx> findCompleted(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COALESCE(SUM(t.grandTotal), 0) FROM Tx t WHERE t.status = 'PENDING' AND t.createdAt >= :start AND t.createdAt < :end")
    BigDecimal pendingTotal(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT t FROM Tx t ORDER BY t.createdAt DESC")
    List<Tx> findRecentTransactions(Pageable pageable);

    @Query("SELECT t FROM Tx t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:search IS NULL OR LOWER(t.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:start IS NULL OR t.createdAt >= :start) AND " +
           "(:end IS NULL OR t.createdAt <= :end) " +
           "ORDER BY t.createdAt DESC")
    List<Tx> searchTransactions(@Param("status") TxStatus status,
                                @Param("search") String search,
                                @Param("start") Instant start,
                                @Param("end") Instant end);

    long countByStatus(TxStatus status);
}
