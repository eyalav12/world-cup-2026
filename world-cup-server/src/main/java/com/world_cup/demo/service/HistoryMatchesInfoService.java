package com.world_cup.demo.service;

import com.world_cup.demo.client.FootballDataClient;
import com.world_cup.demo.dto.MatchDto;
import com.world_cup.demo.dto.MatchResultsContainer;
import com.world_cup.demo.entities.HistoryMatchData;
import com.world_cup.demo.entities.Team;
import com.world_cup.demo.repositories.HistoryMatchDataRepository;
import com.world_cup.demo.repositories.TeamRepository;
import com.world_cup.demo.util.apiUtils.FootballDataApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryMatchesInfoService {
    private final HistoryMatchDataRepository historyMatchDataRepository;
    private final FootballDataApiUtil footballDataApiUtil;
    private final FootballDataClient footballDataClient;
    private final RedisTemplate redisTemplate;
    private final TeamRepository teamRepository;
    private static final Logger logger = LoggerFactory.getLogger(HistoryMatchesInfoService.class);

    public HistoryMatchesInfoService(
            FootballDataApiUtil footballDataApiUtil,
            FootballDataClient footballDataClient,
            HistoryMatchDataRepository historyMatchDataRepository,
            RedisTemplate redisTemplate,
            TeamRepository teamRepository
    ) {
        this.footballDataApiUtil = footballDataApiUtil;
        this.footballDataClient = footballDataClient;
        this.historyMatchDataRepository = historyMatchDataRepository;
        this.redisTemplate = redisTemplate;
        this.teamRepository = teamRepository;
    }

    public List<HistoryMatchData> getHeadToHead(String teamA, String teamB) {
        try {
            return historyMatchDataRepository.getHeadToHead(teamA, teamB);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<HistoryMatchData> getTeamLastMatches(String teamName) {
        try {
            return historyMatchDataRepository.getTeamLastMatches(teamName);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Collections.emptyList();
        }
    }

    private Integer getTeamIdFromDb(String teamName) {
        Team team = teamRepository.findByTeamName(teamName).orElse(null);
        if (team == null) {
            return -1;
        }
        return team.getTeamId();
    }

    public MatchResultsContainer<MatchDto> getTeamsLastMatchesFromApi(String teamA, String teamB) {
        try {
            List<MatchDto> teamAMatches = getTeamLastMatchesFromApi(teamA, null);
            List<MatchDto> teamBMatches = getTeamLastMatchesFromApi(teamB, null);
            return new MatchResultsContainer<>(teamAMatches, teamBMatches);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new MatchResultsContainer();
        }
    }

    public List<MatchDto> getTeamLastMatchesFromApi(String teamName, Integer limit) {
        try {
            Integer teamId = getTeamIdFromDb(teamName);
            if (teamId == -1) {
                logger.info("could not find team id for team {}", teamName);
                return Collections.emptyList();
            }

            LocalDate today = LocalDate.now();
            LocalDate twoYearsAgo = today.minusYears(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String apiResponse = footballDataClient.fetchTeamMatches(
                    teamId,
                    twoYearsAgo.format(formatter),
                    today.format(formatter)
            );

            List<MatchDto> matches = footballDataApiUtil.convertApiResponseToDtoMatchResult(apiResponse);
            List<MatchDto> finished = matches.stream()
                    .filter(m -> m.getStatus() != null && "FINISHED".equalsIgnoreCase(m.getStatus()))
                    .sorted(Comparator.comparing(MatchDto::getMatchDate, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            if (limit != null && limit > 0 && finished.size() > limit) {
                return finished.subList(0, limit);
            }
            return finished;
        } catch (Exception e) {
            logger.error("failed to fetch API last matches for team {}", teamName, e);
            return Collections.emptyList();
        }
    }
}
