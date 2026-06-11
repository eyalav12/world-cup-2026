package com.world_cup.demo.dto;

public record Article(
        String title,
        String description,
        String url,
        String publishedAt,
        String urlToImage,
        Source source
){}