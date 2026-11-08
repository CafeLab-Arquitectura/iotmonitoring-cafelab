package com.cafemetrix.cafelab.monitoring.application.internal.commandservices;

import com.cafemetrix.cafelab.monitoring.application.internal.outboundservices.acl.ExternalProductionService;
import com.cafemetrix.cafelab.monitoring.domain.model.aggregates.TelemetryRecord;
import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetryRecordCommandServiceImplTest {

    @Mock
    private TelemetryRecordRepository telemetryRepository;

    @Mock
    private ExternalProductionService externalProductionService;

    @InjectMocks
    private TelemetryRecordCommandServiceImpl commandService;

    @Test
    void shouldRejectTelemetryForUnknownCoffeeLot() {
        var command = new CreateTelemetryRecordCommand(99L, 24.0, 60.0, LocalDateTime.of(2026, 6, 15, 11, 0, 0));
        when(externalProductionService.existsCoffeeLot(99L)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));

        assertEquals("Cannot record telemetry. Coffee Lot with ID 99 does not exist.", exception.getMessage());
        verify(telemetryRepository, never()).save(any());
    }

    @Test
    void shouldPersistTelemetryForExistingCoffeeLot() {
        var command = new CreateTelemetryRecordCommand(7L, 24.0, 60.0, LocalDateTime.of(2026, 6, 15, 11, 0, 0));
        when(externalProductionService.existsCoffeeLot(7L)).thenReturn(true);
        when(telemetryRepository.findBySensorIdAndSequence("UNKNOWN", 0L)).thenReturn(Optional.empty());
        when(telemetryRepository.save(any(TelemetryRecord.class))).thenAnswer(i -> i.getArgument(0));

        commandService.handle(command);

        var captor = ArgumentCaptor.forClass(TelemetryRecord.class);
        verify(telemetryRepository).save(captor.capture());
        assertEquals(7L, captor.getValue().getCoffeeLotId());
        assertEquals(24.0, captor.getValue().getTemperature());
        assertEquals(60.0, captor.getValue().getHumidity());
        assertEquals("UNKNOWN", captor.getValue().getSensorId());
        assertEquals(0L, captor.getValue().getSequence());
        assertNotNull(captor.getValue().getMeasuredAt());
        assertNotNull(captor.getValue().getReceivedAt());
        assertEquals(captor.getValue().getMeasuredAt(), captor.getValue().getTimestamp());
    }

    @Test
    void shouldReturnExistingRecordWhenDuplicateSensorIdAndSequenceForIdempotency() {
        var measuredAt = LocalDateTime.of(2026, 6, 15, 11, 0, 0);
        var receivedAt = LocalDateTime.of(2026, 6, 15, 11, 0, 5);
        var command = new CreateTelemetryRecordCommand("SENSOR-01", 7L, 24.0, 60.0, measuredAt, receivedAt, 42L);

        var existingRecord = new TelemetryRecord(command);

        when(externalProductionService.existsCoffeeLot(7L)).thenReturn(true);
        when(telemetryRepository.findBySensorIdAndSequence("SENSOR-01", 42L))
                .thenReturn(Optional.of(existingRecord));

        var result = commandService.handle(command);

        assertTrue(result.isPresent());
        assertSame(existingRecord, result.get());
        verify(telemetryRepository, never()).save(any());
    }

    @Test
    void shouldPersistFullTelemetryRecordWhenNotExisting() {
        var measuredAt = LocalDateTime.of(2026, 6, 15, 11, 0, 0);
        var receivedAt = LocalDateTime.of(2026, 6, 15, 11, 0, 5);
        var command = new CreateTelemetryRecordCommand("SENSOR-02", 8L, 26.5, 65.0, measuredAt, receivedAt, 100L);

        when(externalProductionService.existsCoffeeLot(8L)).thenReturn(true);
        when(telemetryRepository.findBySensorIdAndSequence("SENSOR-02", 100L))
                .thenReturn(Optional.empty());
        when(telemetryRepository.save(any(TelemetryRecord.class))).thenAnswer(i -> i.getArgument(0));

        var result = commandService.handle(command);

        assertTrue(result.isPresent());
        var captor = ArgumentCaptor.forClass(TelemetryRecord.class);
        verify(telemetryRepository).save(captor.capture());
        assertEquals("SENSOR-02", captor.getValue().getSensorId());
        assertEquals(100L, captor.getValue().getSequence());
        assertEquals(measuredAt, captor.getValue().getMeasuredAt());
        assertEquals(receivedAt, captor.getValue().getReceivedAt());
        assertEquals(measuredAt, captor.getValue().getTimestamp());
    }
}
