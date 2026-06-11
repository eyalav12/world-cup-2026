package com.world_cup.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Table(Integer position,Team team, Integer playedGames,String form,Integer won,
                    Integer draw, Integer lost,Integer points, Integer goalsFor,Integer goalsAgainst, Integer goalsDifference) {
}
