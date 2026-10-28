package com.chillpops.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Entity
@Table(name = "payment_details")
@Getter
@Setter
@NoArgsConstructor
public class PaymentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    @JsonIgnore
    private Tx transaction;

    @Column(nullable = false, length = 15)
    private String method;

    @Column(name = "upi_reference", length = 64)
    private String upiReference;

    @Column(name = "confirmed_by", nullable = false, length = 50)
    private String confirmedBy;

    @Column(name = "confirmed_at", nullable = false)
    private Timestamp confirmedAt;
}
