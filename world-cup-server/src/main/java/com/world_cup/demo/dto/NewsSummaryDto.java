package com.world_cup.demo.dto;

import java.util.List;

public record NewsSummaryDto(
        String teamName,
        List<NewsSummaryItem> headlines
) {}
