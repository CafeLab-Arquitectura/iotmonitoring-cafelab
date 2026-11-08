package com.cafemetrix.cafelab.monitoring.interfaces.rest.resources;

import java.time.LocalDateTime;

public record TelemetryRecordResource(
        Long id,
        String sensorId,
        Long coffeeLotId,
        Double temperature,
        Double humidity,
        LocalDateTime measuredAt,
        LocalDateTime receivedAt,
        Long sequence,
        LocalDateTime timestamp
) {

    public TelemetryRecordResource(
            Long id,
            Long coffeeLotId,
            Double temperature,
            Double humidity,
            LocalDateTime timestamp
    ) {
        this(id, null, coffeeLotId, temperature, humidity, timestamp, null, null, timestamp);
    }
}
