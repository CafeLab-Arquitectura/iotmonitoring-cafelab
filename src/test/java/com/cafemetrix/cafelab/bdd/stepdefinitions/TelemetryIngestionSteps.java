package com.cafemetrix.cafelab.bdd.stepdefinitions;

import com.cafemetrix.cafelab.bdd.CucumberSpringConfiguration;
import com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRecordRepository;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TelemetryIngestionSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CucumberSpringConfiguration cucumberConfig;

    @Autowired
    private TelemetryRecordRepository telemetryRecordRepository;

    private ResultActions latestResponse;
    private Long currentLotId;
    private String currentSensorId;
    private Long currentSequence;

    @Dado("que existe un lote de café con ID {long} en el sistema")
    public void queExisteUnLoteDeCafeConIDEnElSistema(Long lotId) {
        this.currentLotId = lotId;
        cucumberConfig.mockExistingCoffeeLot(lotId);
    }

    @Cuando("el sensor {string} envía una lectura con secuencia {long}, temperatura {double} °C y humedad {double} %")
    public void elSensorEnviaUnaLecturaConSecuenciaTemperaturaYHumedad(
            String sensorId, Long sequence, Double temperature, Double humidity) throws Exception {
        this.currentSensorId = sensorId;
        this.currentSequence = sequence;

        String jsonPayload = String.format("""
                {
                    "sensorId": "%s",
                    "coffeeLotId": %d,
                    "temperature": %s,
                    "humidity": %s,
                    "measuredAt": "%s",
                    "sequence": %d
                }
                """, sensorId, currentLotId, temperature, humidity, LocalDateTime.now().toString(), sequence);

        latestResponse = mockMvc.perform(post("/api/v1/telemetry-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload));
    }

    @Entonces("el sistema responde con código de estado HTTP {int}")
    public void elSistemaRespondeConCodigoDeEstadoHTTP(int statusCode) throws Exception {
        latestResponse.andExpect(status().is(statusCode));
    }

    @Y("la lectura queda registrada con sensor {string} y secuencia {long}")
    public void laLecturaQuedaRegistradaConSensorYSecuencia(String sensorId, Long sequence) {
        var record = telemetryRecordRepository.findBySensorIdAndSequence(sensorId, sequence);
        assertTrue(record.isPresent(), "La lectura debería estar persistida en la base de datos");
        assertEquals(sensorId, record.get().getSensorId());
        assertEquals(sequence, record.get().getSequence());
    }

    @Y("ya se encuentra registrada una lectura del sensor {string} con secuencia {long}")
    public void yaSeEncuentraRegistradaUnaLecturaDelSensorConSecuencia(String sensorId, Long sequence) throws Exception {
        elSensorEnviaUnaLecturaConSecuenciaTemperaturaYHumedad(sensorId, sequence, 24.5, 60.0);
        latestResponse.andExpect(status().isCreated());
    }

    @Cuando("el sensor reenvía la misma lectura con secuencia {long}, temperatura {double} °C y humedad {double} %")
    public void elSensorReenviaLaMismaLectura(Long sequence, Double temperature, Double humidity) throws Exception {
        elSensorEnviaUnaLecturaConSecuenciaTemperaturaYHumedad(currentSensorId, sequence, temperature, humidity);
    }

    @Entonces("el sistema responde con código de estado HTTP 200 o 201")
    public void elSistemaRespondeConCodigo200o201() {
        int status = latestResponse.andReturn().getResponse().getStatus();
        assertTrue(status == 200 || status == 201, "El código de respuesta debe ser 200 o 201 para inserción idempotente");
    }

    @Y("no se duplica el registro en la base de datos")
    public void noSeDuplicaElRegistroEnLaBaseDeDatos() {
        long count = telemetryRecordRepository.findAll().stream()
                .filter(r -> currentSensorId.equals(r.getSensorId()) && currentSequence.equals(r.getSequence()))
                .count();
        assertEquals(1, count, "No debe haber más de un registro con la misma combinación (sensorId, sequence)");
    }
}

