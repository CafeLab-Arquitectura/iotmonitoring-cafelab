package com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.monitoring.domain.model.aggregates.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetryRecordRepository extends JpaRepository<TelemetryRecord, Long> {
    List<TelemetryRecord> findByCoffeeLotIdOrderByTimestampAsc(Long coffeeLotId);

    boolean existsByCoffeeLotId(Long coffeeLotId);

    Optional<TelemetryRecord> findBySensorIdAndSequence(String sensorId, Long sequence);

    @Modifying
    @Query("DELETE FROM TelemetryRecord t WHERE t.measuredAt < :cutoff")
    int deleteByMeasuredAtBefore(@Param("cutoff") LocalDateTime cutoff);
}
