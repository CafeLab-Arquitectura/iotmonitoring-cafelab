package com.cafemetrix.cafelab.bdd;

import com.cafemetrix.cafelab.iotmonitoring.IotmonitoringApplication;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeLotSummary;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.Mockito.when;

@CucumberContextConfiguration
@SpringBootTest(classes = IotmonitoringApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @MockBean
    private CoffeeproductionContextFacade coffeeproductionContextFacade;

    public void mockExistingCoffeeLot(Long lotId) {
        when(coffeeproductionContextFacade.getCoffeeLotById(lotId))
                .thenReturn(Optional.of(new CoffeeLotSummary(lotId, 1L)));
    }
}
