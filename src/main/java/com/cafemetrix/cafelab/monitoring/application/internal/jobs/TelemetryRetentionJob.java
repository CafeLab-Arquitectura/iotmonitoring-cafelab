package com.cafemetrix.cafelab.monitoring.application.internal.jobs;

import com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@Transactional
public class TelemetryRetentionJob {

    private static final Logger log = LoggerFactory.getLogger(TelemetryRetentionJob.class);

    private final TelemetryRecordRepository telemetryRepository;

    public TelemetryRetentionJob(TelemetryRecordRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    @Scheduled(cron = "${cafelab.telemetry.retention.cron:0 0 2 * * ?}")
    public void purgeOldRecords() {
        LocalDateTime cutoff = LocalDateTime.now(ZoneOffset.UTC).minusDays(60);
        int deleted = telemetryRepository.deleteByMeasuredAtBefore(cutoff);
        log.info("TelemetryRetentionJob: Purgados {} registros mayores a 60 días.", deleted);
    }
}

