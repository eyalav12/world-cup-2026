package com.world_cup.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EspnApiClient extends BaseApiClient {

    private static final int GENERAL_NEWS_LIMIT = 20;
    private static final int TEAM_NEWS_LIMIT = 10;
    private static final int TEAMS_LIMIT = 100;

    @Value("${espn.api.base-url:https://site.api.espn.com/apis/site/v2/sports/soccer/fifa.world}")
    private String baseUrl;

    public String fetchGeneralNews() {
        String url = String.format("%s/news?limit=%d", baseUrl, GENERAL_NEWS_LIMIT);
        return executeGet(url);
    }

    public String fetchNewsByTeamId(String espnTeamId) {
        String url = String.format("%s/news?team=%s&limit=%d", baseUrl, espnTeamId, TEAM_NEWS_LIMIT);
        return executeGet(url);
    }

    public String fetchTeams() {
        String url = String.format("%s/teams?limit=%d", baseUrl, TEAMS_LIMIT);
        return executeGet(url);
    }
}
