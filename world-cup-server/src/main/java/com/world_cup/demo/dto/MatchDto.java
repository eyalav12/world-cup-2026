package com.world_cup.demo.dto;

public class MatchDto {
    private String result;
    private String score;
    private String awayTeam;
    private String matchDate;
    private Integer matchId;
    private String stage;
    private String competition;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MatchDto() {
    }

    private String homeTeam;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }


    public MatchDto(String result, String score, String awayTeam, String matchDate, Integer matchId, String stage, String competition, String status, String homeTeam) {
        this.result = result;
        this.score = score;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchId = matchId;
        this.stage = stage;
        this.competition = competition;
        this.status = status;
        this.homeTeam = homeTeam;
    }


    public MatchDto(String result, String score, String homeTeam, String awayTeam, String matchDate, Integer matchId, String stage, String competition,String status) {
        this.result = result;
        this.score = score;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchId = matchId;
        this.stage = stage;
        this.competition = competition;
        this.status = status;
    }

}
