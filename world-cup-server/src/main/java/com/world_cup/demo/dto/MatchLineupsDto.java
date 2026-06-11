package com.world_cup.demo.dto;

import java.util.List;

public record MatchLineupsDto(
        Integer matchId,
        String homeTeam,
        String awayTeam,
        List<LineupPlayerDto> homeStarting,
        List<LineupPlayerDto> awayStarting,
        List<LineupPlayerDto> homeBench,
        List<LineupPlayerDto> awayBench
) {}
