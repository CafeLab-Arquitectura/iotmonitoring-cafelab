package com.cafemetrix.cafelab.monitoring.application.internal.jobs;

import com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetryRetentionJobTest {

    @Mock
    private TelemetryRecordRepository telemetryRepository;

    @InjectMocks
    private TelemetryRetentionJob retentionJob;

    @Test
    void shouldPurgeRecordsOlderThan60Days() {
        when(telemetryRepository.deleteByMeasuredAtBefore(any(LocalDateTime.class))).thenReturn(15);

        retentionJob.purgeOldRecords();

        var captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(telemetryRepository).deleteByMeasuredAtBefore(captor.capture());

        var capturedCutoff = captor.getValue();
        var expectedCutoff = LocalDateTime.now(ZoneOffset.UTC).minusDays(60);

        // Captured cutoff should be within 2 seconds of expectedCutoff
        long diffSeconds = Math.abs(java.time.Duration.between(expectedCutoff, capturedCutoff).toSeconds());
        assertTrue(diffSeconds < 2, "Cutoff should be approximately 60 days before now in UTC");
    }
}

