package com.world_cup.demo.service.startupservices;

import com.world_cup.demo.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class StartUpMatchSyncLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartUpMatchSyncLoader.class);

    private final MatchService matchService;

    public StartUpMatchSyncLoader(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public void run(String... args) {
        logger.info("Syncing upcoming matches from Football Data API on startup …");
        matchService.syncUpcomingMatchesFromApi();
    }
}
