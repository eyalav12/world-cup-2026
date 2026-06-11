package com.world_cup.demo.mapper;

import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.dto.NewsSummaryDto;
import com.world_cup.demo.dto.NewsSummaryItem;

import java.util.Collections;
import java.util.List;

public final class NewsSummaryMapper {

    public static final int GENERAL_AGENT_HEADLINE_LIMIT = 5;
    public static final int TEAM_AGENT_HEADLINE_LIMIT = 3;

    private NewsSummaryMapper() {
    }

    public static NewsSummaryDto toSummary(NewsResponse newsResponse, String teamName, int headlineLimit) {
        if (newsResponse == null || newsResponse.articles() == null || newsResponse.articles().isEmpty()) {
            return new NewsSummaryDto(teamName, Collections.emptyList());
        }

        List<NewsSummaryItem> headlines = newsResponse.articles().stream()
                .limit(headlineLimit)
                .map(article -> new NewsSummaryItem(
                        article.title(),
                        article.description(),
                        article.publishedAt(),
                        article.url()
                ))
                .toList();

        return new NewsSummaryDto(teamName, headlines);
    }
}
