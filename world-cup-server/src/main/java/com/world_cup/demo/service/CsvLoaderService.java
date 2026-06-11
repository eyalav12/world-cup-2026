package com.world_cup.demo.service;

import com.opencsv.CSVReader;
import com.world_cup.demo.entities.HistoryMatchData;
import com.world_cup.demo.repositories.HistoryMatchDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(CsvLoaderService.class);
    private static final String DEFAULT_CLASSPATH_CSV = "data/world-cup-history-matches.csv";

    private final HistoryMatchDataRepository historyMatchDataRepository;

    @Value("${history.csv.path:}")
    private String historyCsvPath;

    public CsvLoaderService(HistoryMatchDataRepository historyMatchDataRepository) {
        this.historyMatchDataRepository = historyMatchDataRepository;
    }

    /** Optional CSV import if SQL seed was not bundled and table is still empty. */
    public void handleFileLoader() {
        if (historyMatchDataRepository.count() > 0) {
            return;
        }

        try (Reader reader = openCsvReader()) {
            if (reader == null) {
                logger.info("No history CSV configured, skipping CSV import");
                return;
            }

            List<String> headers = new ArrayList<>();
            List<List<String>> rowsData = new ArrayList<>();

            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] nextRecord;
                boolean firstRow = true;
                while ((nextRecord = csvReader.readNext()) != null) {
                    List<String> currentRow = new ArrayList<>();
                    for (String cell : nextRecord) {
                        if (firstRow) {
                            headers.add(cell);
                        } else {
                            currentRow.add(cell);
                        }
                    }
                    if (!firstRow) {
                        rowsData.add(currentRow);
                    }
                    firstRow = false;
                }
            }

            List<HistoryMatchData> list = toEntities(rowsData, headers);
            saveToDB(list);
            logger.info("CSV history import finished ({} rows)", historyMatchDataRepository.count());
        } catch (Exception e) {
            logger.error("Failed to load history from CSV", e);
        }
    }

    private Reader openCsvReader() throws Exception {
        if (historyCsvPath != null && !historyCsvPath.isBlank()) {
            Path path = Path.of(historyCsvPath);
            if (Files.isRegularFile(path)) {
                logger.info("Loading history CSV from {}", path);
                return Files.newBufferedReader(path);
            }
            logger.warn("history.csv.path set but file not found: {}", historyCsvPath);
        }

        ClassPathResource resource = new ClassPathResource(DEFAULT_CLASSPATH_CSV);
        if (resource.exists()) {
            logger.info("Loading history CSV from classpath {}", DEFAULT_CLASSPATH_CSV);
            return new InputStreamReader(resource.getInputStream());
        }

        return null;
    }

    private void saveToDB(List<HistoryMatchData> historyMatchDataList) {
        for (HistoryMatchData historyMatchData : historyMatchDataList) {
            historyMatchDataRepository.save(historyMatchData);
        }
    }

    private List<HistoryMatchData> toEntities(List<List<String>> rowsData, List<String> headers)
            throws NoSuchFieldException, IllegalAccessException {
        List<Map<String, String>> list = new ArrayList<>();
        for (List<String> row : rowsData) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < row.size(); i++) {
                if (i == 0 || i == 9 || i == 10 || i == 12 || i == 17 || i == 20) {
                    continue;
                }
                String key = headers.get(i);
                String first = key.substring(0, 1).toLowerCase();
                String rest = key.substring(1).replace(" ", "");
                map.put(first + rest, row.get(i));
            }
            list.add(map);
        }

        List<HistoryMatchData> historyMatchDataList = new ArrayList<>();
        for (Map<String, String> map : list) {
            historyMatchDataList.add(new HistoryMatchData(map));
        }
        return historyMatchDataList;
    }
}
