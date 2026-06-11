package com.world_cup.demo.dto;

import java.util.List;

public class MatchResultsContainer<T> {
    public MatchResultsContainer() {
    }

    public MatchResultsContainer(List<T> homeTeamResults, List<T> awayTeamResults) {
        this.homeTeamResults = homeTeamResults;
        this.awayTeamResults = awayTeamResults;
    }

    public List<T> getHomeTeamResults() {
        return homeTeamResults;
    }

    public void setHomeTeamResults(List<T> homeTeamResults) {
        this.homeTeamResults = homeTeamResults;
    }

    public List<T> getAwayTeamResults() {
        return awayTeamResults;
    }

    public void setAwayTeamResults(List<T> awayTeamResults) {
        this.awayTeamResults = awayTeamResults;
    }

    private List<T> homeTeamResults;
    private List<T> awayTeamResults;
}
