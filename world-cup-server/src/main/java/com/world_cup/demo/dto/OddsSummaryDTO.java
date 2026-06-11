package com.world_cup.demo.dto;
import java.util.List;
import java.util.Map;

public class OddsSummaryDTO {
    private Integer matchId;
    private Map<String, Object> marketAverage;
    private Map<String, Object> marketExtremes;
    private List<SportsbookOdds> topSportsbooks;

    public OddsSummaryDTO() {}

    public OddsSummaryDTO(Integer matchId, Map<String, Object> marketAverage, Map<String, Object> marketExtremes, List<SportsbookOdds> topSportsbooks) {
        this.matchId = matchId;
        this.marketAverage = marketAverage;
        this.marketExtremes = marketExtremes;
        this.topSportsbooks = topSportsbooks;
    }

    // Getters and Setters
    public Integer getMatchId() { return matchId; }
    public void setMatchId(Integer matchId) { this.matchId = matchId; }
    public Map<String, Object> getMarketAverage() { return marketAverage; }
    public void setMarketAverage(Map<String, Object> marketAverage) { this.marketAverage = marketAverage; }
    public Map<String, Object> getMarketExtremes() { return marketExtremes; }
    public void setMarketExtremes(Map<String, Object> marketExtremes) { this.marketExtremes = marketExtremes; }
    public List<SportsbookOdds> getTopSportsbooks() { return topSportsbooks; }
    public void setTopSportsbooks(List<SportsbookOdds> topSportsbooks) { this.topSportsbooks = topSportsbooks; }
}

