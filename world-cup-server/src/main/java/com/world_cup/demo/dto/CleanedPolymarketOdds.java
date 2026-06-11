package com.world_cup.demo.dto;

import java.util.List;

public record CleanedPolymarketOdds(String title, List<CleanedMarket> markets) {}
