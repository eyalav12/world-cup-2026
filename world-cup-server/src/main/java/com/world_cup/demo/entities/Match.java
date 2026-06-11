package com.world_cup.demo.entities;

import com.world_cup.demo.dto.MatchDto;
import jakarta.persistence.*;

@Entity
public class Match {
    public Integer getId() {
        return id;
    }

    public Match(String result, String score, String homeTeam,String awayTeam, String matchDate, Integer matchId, String stage, String competition,String status) {
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

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Match() {
    }

    public Match(Integer id, String result, String score, String awayTeam, String matchDate, Integer matchId, String stage, String competition, String status) {
        this.id = id;
        this.result = result;
        this.score = score;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchId = matchId;
        this.stage = stage;
        this.competition = competition;
        this.status = status;
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String result;
    private String score;
    private String homeTeam;;
    private String awayTeam;
    private String matchDate;
    private Integer matchId;
    private String stage;
    private String competition;
    private String status;
    private String oddsApiId;

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getStatus(){
        return status;
    }

    public String getOddsApiId() {
        return oddsApiId;
    }

    public void setOddsApiId(String oddsApiId) {
        this.oddsApiId = oddsApiId;
    }


    public void setStatus(String status){
        this.status = status;
    }
    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

}
