package com.chillpops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoice_sequence")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceSequence {

    @Id
    @Column(name = "day_key", length = 8)
    private String dayKey;

    @Column(name = "last_no", nullable = false)
    private int lastNo;

    public InvoiceSequence(String dayKey, int lastNo) {
        this.dayKey = dayKey;
        this.lastNo = lastNo;
    }
}
