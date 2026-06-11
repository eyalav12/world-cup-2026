package com.world_cup.demo.util.apiUtils;

import com.world_cup.demo.client.EspnApiClient;
import org.springframework.stereotype.Service;

@Service
public class EspnApiUtil {

    private final EspnApiClient espnApiClient;

    public EspnApiUtil(EspnApiClient espnApiClient) {
        this.espnApiClient = espnApiClient;
    }

    public String httpCallToApiForGeneralNews() {
        return espnApiClient.fetchGeneralNews();
    }

    public String httpCallToApiForTeamNews(String espnTeamId) {
        return espnApiClient.fetchNewsByTeamId(espnTeamId);
    }

    public String httpCallToApiForTeams() {
        return espnApiClient.fetchTeams();
    }
}
