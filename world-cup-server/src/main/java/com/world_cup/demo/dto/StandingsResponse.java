package com.world_cup.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StandingsResponse(List<Standing> standings) {
}
