package com.world_cup.demo.service.startupservices;

import com.world_cup.demo.service.OddsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class StartUpOddsCacheLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartUpOddsCacheLoader.class);

    private final OddsService oddsService;

    @Value("${oddsdata.api.token:}")
    private String oddsApiToken;

    public StartUpOddsCacheLoader(OddsService oddsService) {
        this.oddsService = oddsService;
    }

    @Override
    public void run(String... args) {
        if (oddsApiToken == null || oddsApiToken.isBlank()) {
            logger.warn(
                    "ODDSDATA_API_TOKEN is not set — match odds will stay empty until you add it to .env.prod");
            return;
        }
        logger.info("Running initial bookmaker odds sync on startup …");
        oddsService.getFutureMatchesOddsAndFillCache();
    }
}
