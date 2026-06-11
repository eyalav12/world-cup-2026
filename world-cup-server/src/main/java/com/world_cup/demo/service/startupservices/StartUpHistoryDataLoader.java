package com.world_cup.demo.service.startupservices;

import com.world_cup.demo.repositories.HistoryMatchDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Order(1)
public class StartUpHistoryDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartUpHistoryDataLoader.class);
    private static final String HISTORY_SQL = "db/history_match_data.sql";

    private final HistoryMatchDataRepository historyMatchDataRepository;
    private final DataSource dataSource;

    public StartUpHistoryDataLoader(
            HistoryMatchDataRepository historyMatchDataRepository,
            DataSource dataSource
    ) {
        this.historyMatchDataRepository = historyMatchDataRepository;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        if (historyMatchDataRepository.count() > 0) {
            logger.info("History match data already loaded ({} rows), skipping import", historyMatchDataRepository.count());
            return;
        }

        ClassPathResource script = new ClassPathResource(HISTORY_SQL);
        if (!script.exists()) {
            logger.warn("History SQL not found on classpath ({}), skipping", HISTORY_SQL);
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            logger.info("Importing World Cup history from {} …", HISTORY_SQL);
            ScriptUtils.executeSqlScript(connection, script);
            logger.info("History import finished ({} rows)", historyMatchDataRepository.count());
        } catch (Exception e) {
            logger.error("Failed to import history match data from {}", HISTORY_SQL, e);
        }
    }
}
