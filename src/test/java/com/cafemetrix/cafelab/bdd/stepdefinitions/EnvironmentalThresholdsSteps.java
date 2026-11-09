package com.cafemetrix.cafelab.bdd.stepdefinitions;

import com.cafemetrix.cafelab.bdd.CucumberSpringConfiguration;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EnvironmentalThresholdsSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CucumberSpringConfiguration cucumberConfig;

    private ResultActions latestResponse;
    private Long currentLotId;

    @Dado("que existe un lote de café con ID {long}")
    public void queExisteUnLoteDeCafeConID(Long lotId) throws Exception {
        this.currentLotId = lotId;
        cucumberConfig.mockExistingCoffeeLot(lotId);
        mockMvc.perform(get("/api/v1/environment-thresholds/coffee-lot/" + lotId));
    }

    @Cuando("el usuario solicita los umbrales ambientales para el lote {long}")
    public void elUsuarioSolicitaLosUmbralesAmbientalesParaElLote(Long lotId) throws Exception {
        latestResponse = mockMvc.perform(get("/api/v1/environment-thresholds/coffee-lot/" + lotId));
    }

    @Entonces("el sistema responde con código HTTP {int}")
    public void elSistemaRespondeConCodigoHTTP(int statusCode) throws Exception {
        int actual = latestResponse.andReturn().getResponse().getStatus();
        if (actual != statusCode) {
            System.err.println("ERROR RESPONSE: " + latestResponse.andReturn().getResponse().getContentAsString());
        }
        latestResponse.andExpect(status().is(statusCode));
    }

    @Y("los umbrales contienen temperatura mínima, máxima y humedad máxima configurada")
    public void losUmbralesContienenTemperaturaMinimaMaximaYHumedadMaximaConfigurada() throws Exception {
        latestResponse.andExpect(jsonPath("$.minTemperature", notNullValue()))
                .andExpect(jsonPath("$.maxTemperature", notNullValue()))
                .andExpect(jsonPath("$.maxHumidity", notNullValue()));
    }

    @Cuando("el usuario actualiza los umbrales del lote {long} con temperatura entre {double} °C y {double} °C y humedad máxima {double} %")
    public void elUsuarioActualizaLosUmbralesDelLote(Long lotId, Double minTemp, Double maxTemp, Double maxHum) throws Exception {
        String payload = String.format(java.util.Locale.US, """
                {
                    "coffeeLotId": %d,
                    "minTemperature": %.1f,
                    "maxTemperature": %.1f,
                    "minHumidity": 50.0,
                    "maxHumidity": %.1f,
                    "syncIntervalSeconds": 10
                }
                """, lotId, minTemp, maxTemp, maxHum);

        latestResponse = mockMvc.perform(put("/api/v1/environment-thresholds/coffee-lot/" + lotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));
    }

    @Y("los nuevos umbrales reflejan la humedad máxima de {double} %")
    public void losNuevosUmbralesReflejanLaHumedadMaximaDe(Double maxHum) throws Exception {
        latestResponse.andExpect(jsonPath("$.maxHumidity").value(maxHum));
    }
}
