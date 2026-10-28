package com.chillpops.repository;

import com.chillpops.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
    Optional<PaymentDetail> findByTransactionId(Long transactionId);
}
