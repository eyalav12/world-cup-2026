package com.world_cup.demo.mapper;

import com.world_cup.demo.dto.Article;
import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.dto.Source;
import com.world_cup.demo.dto.espn.EspnArticle;
import com.world_cup.demo.dto.espn.EspnNewsApiResponse;

import java.util.Collections;
import java.util.List;

public final class EspnNewsMapper {

    private EspnNewsMapper() {
    }

    public static NewsResponse toNewsResponse(EspnNewsApiResponse espnResponse) {
        if (espnResponse == null || espnResponse.articles() == null || espnResponse.articles().isEmpty()) {
            return new NewsResponse("ok", Collections.emptyList());
        }

        List<Article> articles = espnResponse.articles().stream()
                .map(EspnNewsMapper::toArticle)
                .toList();

        return new NewsResponse("ok", articles);
    }

    public static Article toArticle(EspnArticle espnArticle) {
        String url = "";
        if (espnArticle.links() != null && espnArticle.links().web() != null) {
            url = espnArticle.links().web().href();
        }

        String imageUrl = null;
        if (espnArticle.images() != null && !espnArticle.images().isEmpty()) {
            imageUrl = espnArticle.images().get(0).url();
        }

        return new Article(
                espnArticle.headline(),
                espnArticle.description(),
                url,
                espnArticle.published(),
                imageUrl,
                new Source("espn", "ESPN")
        );
    }
}
