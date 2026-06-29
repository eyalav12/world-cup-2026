package com.world_cup.demo.mapper;

import com.world_cup.demo.dto.MatchDto;
import com.world_cup.demo.entities.Match;

public final class MatchMapper {

    private MatchMapper() {
    }

    public static Match toEntity(MatchDto dto) {
        return new Match(
                dto.getResult(),
                dto.getScore(),
                dto.getHomeTeam(),
                dto.getAwayTeam(),
                dto.getMatchDate(),
                dto.getMatchId(),
                dto.getStage(),
                dto.getCompetition(),
                dto.getStatus()
        );
    }

    public static MatchDto toDto(Match match) {
        return new MatchDto(
                match.getResult(),
                match.getScore(),
                match.getAwayTeam(),
                match.getMatchDate(),
                match.getMatchId(),
                match.getStage(),
                match.getCompetition(),
                match.getStatus(),
                match.getHomeTeam()
        );
    }

    public static void applyUpdate(Match existing, MatchDto dto) {
        existing.setScore(dto.getScore());
        existing.setResult(dto.getResult());
        existing.setStatus(dto.getStatus());
        if (isNotBlank(dto.getHomeTeam())) {
            existing.setHomeTeam(dto.getHomeTeam());
        }
        if (isNotBlank(dto.getAwayTeam())) {
            existing.setAwayTeam(dto.getAwayTeam());
        }
        if (isNotBlank(dto.getMatchDate())) {
            existing.setMatchDate(dto.getMatchDate());
        }
        if (isNotBlank(dto.getStage())) {
            existing.setStage(dto.getStage());
        }
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
