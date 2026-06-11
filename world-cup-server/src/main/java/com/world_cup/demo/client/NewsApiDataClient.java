package com.world_cup.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class NewsApiDataClient extends BaseApiClient{

    @Value("${newsdata.api.token}")
    private String apiToken;

    @Value("${newsdata.api.base-url}")
    private String baseUrl;

    private final Integer PAGE_SIZE_GENERAL_NEWS = 15;

    private final Integer PAGE_SIZE_TEAM_NEWS = 15;

    private String searchQuery = "World Cup football";

    public String fetchGeneralNews() {
        String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
        String url = String.format("%s?q=%s&language=en&sortBy=publishedAt&pageSize=%d&apiKey=%s",
                baseUrl,
                encodedQuery,
                PAGE_SIZE_GENERAL_NEWS,
                apiToken
        );
        return executeGet(url);
    }

    public String fetchNewsByTeamName(String teamName){
        String rawQuery = String.format("World Cup AND %s AND (injury OR tactics OR lineup)", teamName);
        String encodedQuery = URLEncoder.encode(rawQuery, StandardCharsets.UTF_8);
        String finalUrl = String.format("%s?q=%s&language=en&sortBy=relevancy&pageSize=%d&apiKey=%s",
                baseUrl,
                encodedQuery,
                PAGE_SIZE_TEAM_NEWS,
                apiToken
        );
        return executeGet(finalUrl);
    }

}
