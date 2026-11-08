package com.cafemetrix.cafelab.monitoring.domain.model.aggregates;

import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TelemetryRecordTest {

    @Test
    void shouldCreateTelemetryRecordFromCanonicalCommand() {
        var measuredAt = LocalDateTime.of(2026, 6, 15, 10, 0, 0);
        var receivedAt = LocalDateTime.of(2026, 6, 15, 10, 0, 2);
        var command = new CreateTelemetryRecordCommand("SN-999", 5L, 22.5, 55.0, measuredAt, receivedAt, 1L);

        var record = new TelemetryRecord(command);

        assertEquals("SN-999", record.getSensorId());
        assertEquals(5L, record.getCoffeeLotId());
        assertEquals(22.5, record.getTemperature());
        assertEquals(55.0, record.getHumidity());
        assertEquals(measuredAt, record.getMeasuredAt());
        assertEquals(receivedAt, record.getReceivedAt());
        assertEquals(1L, record.getSequence());
        assertEquals(measuredAt, record.getTimestamp());
    }

    @Test
    void shouldCreateTelemetryRecordFromLegacyCommand() {
        var timestamp = LocalDateTime.of(2026, 6, 15, 10, 0, 0);
        var command = new CreateTelemetryRecordCommand(5L, 22.5, 55.0, timestamp);

        var record = new TelemetryRecord(command);

        assertEquals("UNKNOWN", record.getSensorId());
        assertEquals(5L, record.getCoffeeLotId());
        assertEquals(22.5, record.getTemperature());
        assertEquals(55.0, record.getHumidity());
        assertEquals(timestamp, record.getMeasuredAt());
        assertNotNull(record.getReceivedAt());
        assertEquals(0L, record.getSequence());
        assertEquals(timestamp, record.getTimestamp());
    }
}

