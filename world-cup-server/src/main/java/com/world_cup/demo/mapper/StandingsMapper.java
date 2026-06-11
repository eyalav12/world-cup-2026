package com.world_cup.demo.mapper;

import com.world_cup.demo.dto.Table;
import com.world_cup.demo.entities.GroupStandings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StandingsMapper {

    private static final Pattern GROUP_LETTER = Pattern.compile("(?i)Group\\s+([A-L])");

    private StandingsMapper() {
    }

    /** Align football-data "Group A" with app convention "GROUP_A". */
    public static String normalizeGroupName(String group) {
        if (group == null || group.isBlank()) {
            return group;
        }

        String trimmed = group.trim();
        if (trimmed.toUpperCase().startsWith("GROUP_")) {
            return trimmed.toUpperCase();
        }

        Matcher matcher = GROUP_LETTER.matcher(trimmed);
        if (matcher.matches()) {
            return "GROUP_" + matcher.group(1).toUpperCase();
        }

        return trimmed.replace(" ", "_").toUpperCase();
    }

    public static GroupStandings toEntity(String group, Table row) {
        return new GroupStandings(
                normalizeGroupName(group),
                row.position(),
                row.team().id(),
                row.team().name(),
                row.team().crest(),
                orZero(row.playedGames()),
                row.form(),
                orZero(row.won()),
                orZero(row.draw()),
                orZero(row.lost()),
                orZero(row.points()),
                orZero(row.goalsFor()),
                orZero(row.goalsAgainst()),
                orZero(row.goalsDifference())
        );
    }

    private static Integer orZero(Integer value) {
        return value == null ? 0 : value;
    }

    public static void applyUpdate(GroupStandings existing, GroupStandings incoming) {
        existing.setPosition(incoming.getPosition());
        existing.setName(incoming.getName());
        existing.setCrest(incoming.getCrest());
        existing.setPlayedGames(incoming.getPlayedGames());
        existing.setForm(incoming.getForm());
        existing.setWon(incoming.getWon());
        existing.setDraw(incoming.getDraw());
        existing.setLost(incoming.getLost());
        existing.setPoints(incoming.getPoints());
        existing.setGoalsFor(incoming.getGoalsFor());
        existing.setGoalsAgainst(incoming.getGoalsAgainst());
        existing.setGoalsDifference(incoming.getGoalsDifference());
    }
}
