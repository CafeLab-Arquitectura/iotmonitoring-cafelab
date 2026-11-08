package com.cafemetrix.cafelab.monitoring.domain.model.commands;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record CreateTelemetryRecordCommand(
        String sensorId,
        Long coffeeLotId,
        Double temperature,
        Double humidity,
        LocalDateTime measuredAt,
        LocalDateTime receivedAt,
        Long sequence
) {

    public CreateTelemetryRecordCommand {
        if (coffeeLotId == null) {
            throw new IllegalArgumentException("Coffee lot ID cannot be null");
        }
        if (temperature == null) {
            throw new IllegalArgumentException("Temperature reading cannot be null");
        }
        if (humidity == null) {
            throw new IllegalArgumentException("Humidity reading cannot be null");
        }
        if (receivedAt == null) {
            receivedAt = LocalDateTime.now(ZoneOffset.UTC);
        }
        if (sensorId == null || sensorId.isBlank()) {
            sensorId = "UNKNOWN";
        }
        if (sequence == null) {
            sequence = 0L;
        }
        if (measuredAt == null) {
            throw new IllegalArgumentException("MeasuredAt cannot be null");
        }
    }

    public CreateTelemetryRecordCommand(
            String sensorId,
            Long coffeeLotId,
            Double temperature,
            Double humidity,
            LocalDateTime measuredAt,
            LocalDateTime receivedAt,
            Long sequence,
            LocalDateTime timestamp
    ) {
        this(
                sensorId,
                coffeeLotId,
                temperature,
                humidity,
                measuredAt != null ? measuredAt : timestamp,
                receivedAt != null ? receivedAt : LocalDateTime.now(ZoneOffset.UTC),
                sequence
        );
    }

    public CreateTelemetryRecordCommand(
            Long coffeeLotId,
            Double temperature,
            Double humidity,
            LocalDateTime timestamp
    ) {
        this(
                "UNKNOWN",
                coffeeLotId,
                temperature,
                humidity,
                timestamp,
                LocalDateTime.now(ZoneOffset.UTC),
                0L
        );
    }

    public LocalDateTime timestamp() {
        return measuredAt;
    }
}
