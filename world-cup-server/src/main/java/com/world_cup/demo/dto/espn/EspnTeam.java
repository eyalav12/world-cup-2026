package com.world_cup.demo.dto.espn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EspnTeam(
        String id,
        String displayName,
        String abbreviation
) {}
