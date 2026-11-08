package com.cafemetrix.cafelab.monitoring.application.internal.commandservices;

import com.cafemetrix.cafelab.monitoring.application.internal.outboundservices.acl.ExternalProductionService;
import com.cafemetrix.cafelab.monitoring.domain.model.aggregates.TelemetryRecord;
import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import com.cafemetrix.cafelab.monitoring.domain.services.TelemetryRecordCommandService;
import com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TelemetryRecordCommandServiceImpl implements TelemetryRecordCommandService {

    private final TelemetryRecordRepository telemetryRepository;
    private final ExternalProductionService externalProductionService;

    public TelemetryRecordCommandServiceImpl(
            TelemetryRecordRepository telemetryRepository,
            ExternalProductionService externalProductionService) {
        this.telemetryRepository = telemetryRepository;
        this.externalProductionService = externalProductionService;
    }

    @Override
    @Transactional
    public Optional<TelemetryRecord> handle(CreateTelemetryRecordCommand command) {
        if (!externalProductionService.existsCoffeeLot(command.coffeeLotId())) {
            throw new IllegalArgumentException("Cannot record telemetry. Coffee Lot with ID " + command.coffeeLotId() + " does not exist.");
        }

        var existing = telemetryRepository.findBySensorIdAndSequence(command.sensorId(), command.sequence());
        if (existing.isPresent()) {
            return existing; // Idempotencia: retorna el registro sin duplicar ni lanzar error
        }

        var telemetryRecord = new TelemetryRecord(command);
        var saved = telemetryRepository.save(telemetryRecord);
        return Optional.of(saved != null ? saved : telemetryRecord);
    }
}
