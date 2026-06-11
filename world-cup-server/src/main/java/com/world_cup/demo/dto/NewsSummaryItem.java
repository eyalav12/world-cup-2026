package com.world_cup.demo.dto;

import java.util.List;

public record NewsSummaryItem(
        String headline,
        String description,
        String publishedAt,
        String url
) {}
