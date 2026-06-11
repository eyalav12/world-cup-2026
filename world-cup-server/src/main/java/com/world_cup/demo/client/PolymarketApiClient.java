package com.world_cup.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class PolymarketApiClient extends BaseApiClient{

    @Value("${polymarket.api.base-url}")
    private String baseUrl;

    public String fetchTournamentWinner() {
        String url = String.format("%s=world-cup-winner",baseUrl);
        return executeGet(url);
    }

    public String fetchTopScorer(){
        String url = String.format("%s=world-cup-golden-boot-winner",baseUrl);
        return executeGet(url);
    }

    public String fetchGroupWinner(String groupName){
        String url = String.format("%s=world-cup-%s-winner",baseUrl,groupName);
        return executeGet(url);
    }

    public String fetchAdvancement(String stageKey) {
        String slug = switch (stageKey) {
            case "round-of-16" -> "world-cup-nation-to-reach-round-of-16";
            case "quarterfinals" -> "world-cup-nation-to-reach-quarterfinals";
            case "semifinals" -> "world-cup-nation-to-reach-semifinals";
            case "final" -> "world-cup-nation-to-reach-final";
            default -> throw new IllegalArgumentException("Unknown advancement stage: " + stageKey);
        };
        String url = String.format("%s=%s", baseUrl, slug);
        return executeGet(url);
    }
}
