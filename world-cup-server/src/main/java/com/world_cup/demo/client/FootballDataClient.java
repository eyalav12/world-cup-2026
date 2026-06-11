package com.world_cup.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Component
public class FootballDataClient extends BaseApiClient{

    @Value("${footballdata.api.token}")
    private String apiToken;

    @Value("${footballdata.api.base-url}")
    private String baseUrl;

    public String fetchMatches(String fromDate, String toDate) {
        String url = String.format("%s/competitions/WC/matches?dateFrom=%s&dateTo=%s", baseUrl, fromDate, toDate);
        return executeGet(url);
    }

    public String fetchStandings(){
        String url = String.format("%s/competitions/WC/standings",baseUrl);
        return executeGet(url);
    }

    public String fetchTeamMatches(Integer teamId, String fromDate, String toDate) {
        String url = String.format("%s/teams/%d/matches?dateFrom=%s&dateTo=%s", baseUrl, teamId, fromDate, toDate);
        return executeGet(url);
    }

    public String fetchWorldCupResource(String path) {
        String url = String.format("%s/competitions/WC/%s", baseUrl, path);
        return executeGet(url);
    }

    public String fetchCompetitionFinishedMatches(String competitionCode, String fromDate, String toDate) {
        String url = String.format(
                "%s/competitions/%s/matches?dateFrom=%s&dateTo=%s&status=FINISHED",
                baseUrl,
                competitionCode,
                fromDate,
                toDate
        );
        return executeGet(url);
    }

    public String fetchMatchWithLineups(Integer matchId) {
        String url = String.format("%s/matches/%d", baseUrl, matchId);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("X-Auth-Token", apiToken)
                .header("X-Unfold-Lineups", "true")
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch match lineups from football API", e);
        }
    }

    @Override
    public String executeGet(String url) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("X-Auth-Token", apiToken)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from football API", e);
        }
    }

}

