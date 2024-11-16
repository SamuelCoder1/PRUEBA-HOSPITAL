package com.riwi.Hospital.infrastructure.persistence;

import com.riwi.Hospital.domain.entities.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface AuditRepository extends JpaRepository<AuditRecord, Long> {
    List<AuditRecord> findByTimestampBetween(Date startDate, Date endDate);
}
