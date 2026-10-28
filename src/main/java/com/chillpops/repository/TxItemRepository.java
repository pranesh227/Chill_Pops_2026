package com.chillpops.repository;

import com.chillpops.entity.TxItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TxItemRepository extends JpaRepository<TxItem, Long> {
}
