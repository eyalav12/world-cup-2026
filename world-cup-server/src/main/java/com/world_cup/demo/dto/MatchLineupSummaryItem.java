package com.world_cup.demo.dto;

import java.util.List;

public record MatchLineupSummaryItem(
        String name,
        String position,
        Integer shirtNumber
) {}
