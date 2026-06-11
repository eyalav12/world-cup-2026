package com.world_cup.demo.service.startupservices;

import com.world_cup.demo.repositories.HistoryMatchDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

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
            logger.info("History match data already loaded ({} rows), skipping import",
                    historyMatchDataRepository.count());
            repairCorruptedHistoryScores();
            return;
        }

        ClassPathResource script = new ClassPathResource(HISTORY_SQL);
        if (!script.exists()) {
            logger.warn("History SQL not found on classpath ({}), skipping", HISTORY_SQL);
            return;
        }

        try (Connection connection = dataSource.getConnection();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(script.getInputStream(), StandardCharsets.UTF_8));
             Statement statement = connection.createStatement()) {

            logger.info("Importing World Cup history from {} …", HISTORY_SQL);
            int inserted = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String sql = line.trim();
                if (!sql.startsWith("INSERT INTO")) {
                    continue;
                }
                statement.execute(sql);
                inserted++;
            }

            statement.execute(
                    "SELECT setval(pg_get_serial_sequence('history_match_data', 'id'), "
                            + "COALESCE((SELECT MAX(id) FROM history_match_data), 1))"
            );

            logger.info("History import finished ({} insert statements, {} rows in table)",
                    inserted, historyMatchDataRepository.count());
            repairCorruptedHistoryScores();
        } catch (Exception e) {
            logger.error("Failed to import history match data from {}", HISTORY_SQL, e);
        }
    }

    /** Fix scores like 4???1 → 4-1 from bad CSV/SQL encoding. */
    private void repairCorruptedHistoryScores() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            int updated = statement.executeUpdate(
                    "UPDATE history_match_data SET score = REPLACE(score, '???', '-') "
                            + "WHERE score LIKE '%???%'");
            if (updated > 0) {
                logger.info("Repaired {} history score strings (??? → -)", updated);
            }
        } catch (Exception e) {
            logger.warn("Could not repair history score strings", e);
        }
    }
}
