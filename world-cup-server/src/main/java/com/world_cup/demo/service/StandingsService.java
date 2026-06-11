package com.world_cup.demo.service;

import com.world_cup.demo.client.FootballDataClient;
import com.world_cup.demo.dto.StandingsResponse;
import com.world_cup.demo.entities.GroupStandings;
import com.world_cup.demo.mapper.StandingsMapper;
import com.world_cup.demo.repositories.StandingsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;

@Service
public class StandingsService {

    private FootballDataClient footballDataClient;
    private final StandingsRepository standingsRepository;
    private final Logger logger = LoggerFactory.getLogger(StandingsService.class);

    public StandingsService(FootballDataClient footballDataClient, StandingsRepository standingsRepository) {
        this.footballDataClient = footballDataClient;
        this.standingsRepository = standingsRepository;
    }

    public List<GroupStandings> getAllStandings(){
        try{
            List<GroupStandings> allSortByGroupNameAndPoints = standingsRepository.findAllSortByGroupNameAndPoints();
            return allSortByGroupNameAndPoints;
        }
        catch(Exception e){
            logger.error("failed to get all standings",e);
            return Collections.emptyList();
        }
    }

    public List<GroupStandings> getStandingsByGroup(String groupName){
        try{
            String groupNameAsDb = groupName.toUpperCase().replace(" ","_");
            List<GroupStandings> allSortByGroupNameAndPoints = standingsRepository.findByGroupNameOrderByPoints(groupNameAsDb);
            return allSortByGroupNameAndPoints;
        }
        catch(Exception e){
            logger.error("failed to get standings for group {}",groupName,e);
            return Collections.emptyList();
        }
    }


    @Async
    @Scheduled(fixedRate = 60 * 1000 * 60 * 3)
    public void fetchStandingFromApiAndSaveToDB() {
        try {
            String apiResponse = footballDataClient.fetchStandings();
            ObjectMapper objectMapper = new ObjectMapper();
            StandingsResponse standingsResponse = objectMapper.readValue(apiResponse, StandingsResponse.class);
            saveOrUpdateStandingsToDB(standingsResponse);
        } catch (Exception e) {
            logger.error("failed to fetch standings from api", e);
        }
    }

    @Transactional
    public void saveOrUpdateStandingsToDB(StandingsResponse response) {
        if (response == null || response.standings() == null) {
            logger.warn("No standings data to save");
            return;
        }

        List<GroupStandings> incomingRows = response.standings().stream()
                .flatMap(standing -> standing.table().stream()
                        .map(row -> StandingsMapper.toEntity(standing.group(), row)))
                .toList();

        int inserted = 0;
        int updated = 0;

        for (GroupStandings incoming : incomingRows) {
            GroupStandings existing = standingsRepository
                    .findByGroupNameAndTeamId(incoming.getGroupName(), incoming.getTeamId())
                    .orElse(null);

            if (existing == null) {
                standingsRepository.save(incoming);
                inserted++;
            } else {
                StandingsMapper.applyUpdate(existing, incoming);
                standingsRepository.save(existing);
                updated++;
            }
        }

        logger.info("Standings sync complete — inserted {}, updated {}", inserted, updated);
    }
}
