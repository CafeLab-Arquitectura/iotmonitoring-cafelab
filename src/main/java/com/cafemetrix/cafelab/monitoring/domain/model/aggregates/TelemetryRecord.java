package com.cafemetrix.cafelab.monitoring.domain.model.aggregates;

import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_records", uniqueConstraints = {
        @UniqueConstraint(name = "uk_telemetry_sensor_sequence", columnNames = {"sensor_id", "sequence"})
})
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, length = 50)
    private String sensorId;

    @Column(nullable = false)
    private Long coffeeLotId;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "sequence", nullable = false)
    private Long sequence;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected TelemetryRecord() {
    }

    public TelemetryRecord(CreateTelemetryRecordCommand command) {
        this.sensorId = command.sensorId();
        this.coffeeLotId = command.coffeeLotId();
        this.temperature = command.temperature();
        this.humidity = command.humidity();
        this.measuredAt = command.measuredAt() != null ? command.measuredAt() : command.timestamp();
        this.receivedAt = command.receivedAt();
        this.sequence = command.sequence();
        this.timestamp = this.measuredAt;
    }

    public TelemetryRecord(
            String sensorId,
            Long coffeeLotId,
            Double temperature,
            Double humidity,
            LocalDateTime measuredAt,
            LocalDateTime receivedAt,
            Long sequence) {
        this.sensorId = sensorId;
        this.coffeeLotId = coffeeLotId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.measuredAt = measuredAt;
        this.receivedAt = receivedAt;
        this.sequence = sequence;
        this.timestamp = this.measuredAt;
    }

    @PrePersist
    @PreUpdate
    private void syncTimestamp() {
        if (this.measuredAt != null) {
            this.timestamp = this.measuredAt;
        } else if (this.timestamp != null) {
            this.measuredAt = this.timestamp;
        }
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Long getCoffeeLotId() {
        return coffeeLotId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public Long getSequence() {
        return sequence;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
