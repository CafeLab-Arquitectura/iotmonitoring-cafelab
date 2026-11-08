package com.cafemetrix.cafelab.monitoring.interfaces.rest.transform;

import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import com.cafemetrix.cafelab.monitoring.interfaces.rest.resources.CreateTelemetryRecordResource;

public class CreateTelemetryRecordCommandFromResourceAssembler {

    private CreateTelemetryRecordCommandFromResourceAssembler() {
    }

    public static CreateTelemetryRecordCommand toCommandFromResource(CreateTelemetryRecordResource resource) {
        return new CreateTelemetryRecordCommand(
                resource.sensorId(),
                resource.coffeeLotId(),
                resource.temperature(),
                resource.humidity(),
                resource.measuredAt(),
                resource.receivedAt(),
                resource.sequence(),
                resource.timestamp());
    }

    public static CreateTelemetryRecordCommand toCommand(CreateTelemetryRecordResource resource) {
        return toCommandFromResource(resource);
    }
}

