package com.cafemetrix.cafelab.monitoring.interfaces.rest.resources;

import java.time.LocalDateTime;

public record CreateTelemetryRecordResource(
        String sensorId,
        Long coffeeLotId,
        Double temperature,
        Double humidity,
        LocalDateTime measuredAt,
        LocalDateTime receivedAt,
        Long sequence,
        LocalDateTime timestamp
) {

    public CreateTelemetryRecordResource(
            Long coffeeLotId,
            Double temperature,
            Double humidity,
            LocalDateTime timestamp
    ) {
        this(null, coffeeLotId, temperature, humidity, null, null, null, timestamp);
    }

    public CreateTelemetryRecordResource(
            String sensorId,
            Long coffeeLotId,
            Double temperature,
            Double humidity,
            LocalDateTime measuredAt,
            LocalDateTime receivedAt,
            Long sequence
    ) {
        this(sensorId, coffeeLotId, temperature, humidity, measuredAt, receivedAt, sequence, null);
    }
}
