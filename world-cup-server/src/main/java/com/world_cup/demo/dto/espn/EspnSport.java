package com.world_cup.demo.dto.espn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EspnSport(
        List<EspnLeague> leagues
) {}
