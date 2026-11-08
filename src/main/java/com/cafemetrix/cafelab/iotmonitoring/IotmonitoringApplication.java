package com.cafemetrix.cafelab.iotmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.cafemetrix.cafelab.iotmonitoring",
        "com.cafemetrix.cafelab.monitoring",
        "com.cafemetrix.cafelab.production"
})
@EntityScan(basePackages = {
        "com.cafemetrix.cafelab.iotmonitoring.domain.model",
        "com.cafemetrix.cafelab.monitoring.domain.model"
})
@EnableJpaRepositories(basePackages = {
        "com.cafemetrix.cafelab.iotmonitoring.infrastructure.persistence.jpa.repositories",
        "com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories"
})
@EnableScheduling
public class IotmonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotmonitoringApplication.class, args);
    }

}
