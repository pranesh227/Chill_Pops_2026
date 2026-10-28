package com.chillpops.service;

import com.chillpops.entity.InvoiceSequence;
import com.chillpops.repository.InvoiceSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceNumberService {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final InvoiceSequenceRepository sequenceRepo;
    private final Clock clock;

    public InvoiceNumberService(InvoiceSequenceRepository sequenceRepo, Clock clock) {
        this.sequenceRepo = sequenceRepo;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String next() {
        String dayKey = LocalDate.now(clock).format(DAY_FORMATTER);
        InvoiceSequence seq = sequenceRepo.findByDayKeyForUpdate(dayKey)
                .orElseGet(() -> new InvoiceSequence(dayKey, 0));

        int nextNo = seq.getLastNo() + 1;
        seq.setLastNo(nextNo);
        sequenceRepo.saveAndFlush(seq);

        return String.format("INV-%s-%04d", dayKey, nextNo);
    }
}
