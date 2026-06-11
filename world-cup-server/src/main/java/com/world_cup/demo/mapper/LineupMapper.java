package com.world_cup.demo.mapper;

import com.world_cup.demo.dto.LineupPlayerDto;
import com.world_cup.demo.dto.MatchLineupSummaryDto;
import com.world_cup.demo.dto.MatchLineupSummaryItem;
import com.world_cup.demo.dto.MatchLineupsDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LineupMapper {

    private LineupMapper() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static MatchLineupsDto fromFootballDataMatchResponse(String body, Integer matchId) {
        if (body == null || body.isBlank()) {
            return null;
        }

        try {
            tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();
            Map map = objectMapper.readValue(body, Map.class);

            Map homeTeamMap = (Map) map.get("homeTeam");
            Map awayTeamMap = (Map) map.get("awayTeam");
            if (homeTeamMap == null || awayTeamMap == null) {
                return null;
            }

            List<LineupPlayerDto> homeStarting = parsePlayers((List<Map>) homeTeamMap.get("lineup"));
            List<LineupPlayerDto> awayStarting = parsePlayers((List<Map>) awayTeamMap.get("lineup"));
            List<LineupPlayerDto> homeBench = parsePlayers((List<Map>) homeTeamMap.get("bench"));
            List<LineupPlayerDto> awayBench = parsePlayers((List<Map>) awayTeamMap.get("bench"));

            if (homeStarting.isEmpty() && awayStarting.isEmpty()) {
                return null;
            }

            return new MatchLineupsDto(
                    matchId,
                    (String) homeTeamMap.get("name"),
                    (String) awayTeamMap.get("name"),
                    homeStarting,
                    awayStarting,
                    homeBench,
                    awayBench
            );
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    private static List<LineupPlayerDto> parsePlayers(List<Map> players) {
        if (players == null || players.isEmpty()) {
            return Collections.emptyList();
        }

        return players.stream()
                .map(player -> {
                    Number idNum = (Number) player.get("id");
                    Number shirtNum = (Number) player.get("shirtNumber");
                    return new LineupPlayerDto(
                            idNum == null ? null : idNum.intValue(),
                            (String) player.get("name"),
                            (String) player.get("position"),
                            shirtNum == null ? null : shirtNum.intValue()
                    );
                })
                .toList();
    }

    public static MatchLineupSummaryDto toSummary(MatchLineupsDto lineups) {
        if (lineups == null) {
            return null;
        }

        return new MatchLineupSummaryDto(
                lineups.matchId(),
                lineups.homeTeam(),
                lineups.awayTeam(),
                toSummaryItems(lineups.homeStarting()),
                toSummaryItems(lineups.awayStarting())
        );
    }

    private static List<MatchLineupSummaryItem> toSummaryItems(List<LineupPlayerDto> players) {
        if (players == null || players.isEmpty()) {
            return Collections.emptyList();
        }

        return players.stream()
                .map(player -> new MatchLineupSummaryItem(
                        player.name(),
                        player.position(),
                        player.shirtNumber()
                ))
                .toList();
    }
}
