package com.cafemetrix.cafelab.monitoring.domain.model.commands;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateTelemetryRecordCommandTest {

    @Test
    void shouldInitializeWithCanonicalValues() {
        var measuredAt = LocalDateTime.of(2026, 6, 15, 10, 0, 0);
        var receivedAt = LocalDateTime.of(2026, 6, 15, 10, 0, 1);
        var command = new CreateTelemetryRecordCommand("SN-123", 10L, 20.0, 50.0, measuredAt, receivedAt, 5L);

        assertEquals("SN-123", command.sensorId());
        assertEquals(10L, command.coffeeLotId());
        assertEquals(20.0, command.temperature());
        assertEquals(50.0, command.humidity());
        assertEquals(measuredAt, command.measuredAt());
        assertEquals(receivedAt, command.receivedAt());
        assertEquals(5L, command.sequence());
        assertEquals(measuredAt, command.timestamp());
    }

    @Test
    void shouldInitializeMeasuredAtFromTimestampWhenMeasuredAtIsNull() {
        var timestamp = LocalDateTime.of(2026, 6, 15, 10, 0, 0);
        var command = new CreateTelemetryRecordCommand("SN-123", 10L, 20.0, 50.0, null, null, 5L, timestamp);

        assertEquals(timestamp, command.measuredAt());
        assertNotNull(command.receivedAt());
    }

    @Test
    void shouldInitializeFromLegacyConstructor() {
        var timestamp = LocalDateTime.of(2026, 6, 15, 10, 0, 0);
        var command = new CreateTelemetryRecordCommand(10L, 20.0, 50.0, timestamp);

        assertEquals("UNKNOWN", command.sensorId());
        assertEquals(10L, command.coffeeLotId());
        assertEquals(20.0, command.temperature());
        assertEquals(50.0, command.humidity());
        assertEquals(timestamp, command.measuredAt());
        assertNotNull(command.receivedAt());
        assertEquals(0L, command.sequence());
        assertEquals(timestamp, command.timestamp());
    }

    @Test
    void shouldThrowWhenRequiredFieldsAreNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateTelemetryRecordCommand("SN-1", null, 20.0, 50.0, LocalDateTime.now(), null, 1L));

        assertThrows(IllegalArgumentException.class, () ->
                new CreateTelemetryRecordCommand("SN-1", 1L, null, 50.0, LocalDateTime.now(), null, 1L));

        assertThrows(IllegalArgumentException.class, () ->
                new CreateTelemetryRecordCommand("SN-1", 1L, 20.0, null, LocalDateTime.now(), null, 1L));

        assertThrows(IllegalArgumentException.class, () ->
                new CreateTelemetryRecordCommand("SN-1", 1L, 20.0, 50.0, null, null, 1L));
    }
}

