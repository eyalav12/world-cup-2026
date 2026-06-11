package com.world_cup.demo.dto.espn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EspnArticle(
        String headline,
        String description,
        String published,
        EspnLinks links,
        List<EspnImage> images
) {}
