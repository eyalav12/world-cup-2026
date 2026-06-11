package com.world_cup.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class OddsApiClient extends BaseApiClient{

    @Value("${oddsdata.api.token}")
    private String apiToken;

    @Value("${oddsdata.api.base-url}")
    private String baseUrl;

    public String fetchMatchesOdds() {
        String url = String.format("%s/sports/soccer_fifa_world_cup/odds/?apiKey=%s&regions=us&markets=h2h&oddsFormat=american",baseUrl,apiToken);
        return executeGet(url);
    }

}
