package com.example.CardCostApi.config;

import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.repository.ClearingCostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Slf4j
public class DataInitializer {

    // Spring application context is fully initialized
    // Seed data to the clearing cost table with the application runner run method.
    @Bean
    public ApplicationRunner initDBTable (ClearingCostRepository clearingCostRepository) {
        return args -> {
            log.info("DataInitializer: Checking if ClearingCost table contains data...");
            if(clearingCostRepository.count() == 0) {
                List<ClearingCost> initialClearingCosts = List.of(
                        new ClearingCost("US", BigDecimal.valueOf(5.00)),
                        new ClearingCost("GR", BigDecimal.valueOf(15.00)),
                        new ClearingCost("OTH", BigDecimal.valueOf(10.00))
                );
                clearingCostRepository.saveAll(initialClearingCosts);
                log.info("DataInitializer: Initialized ClearingCost table.");
            }
        };
    }
}
