package com.world_cup.demo.dto;

import java.util.List;

public record MatchLineupSummaryDto(
        Integer matchId,
        String homeTeam,
        String awayTeam,
        List<MatchLineupSummaryItem> homeStarting,
        List<MatchLineupSummaryItem> awayStarting
) {}
