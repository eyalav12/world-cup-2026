package com.world_cup.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
public class HistoryMatchData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tournamentId;
    private String tournamentName;
    private String matchId;
    private String matchName;
    private String stageName;
    private String groupName;
    private String groupStage;
    private String knockoutStage;
    private LocalDate matchDate;
    private String stadiumId;
    private String stadiumName;
    private String cityName;
    private String countryName;
    private String homeTeamName;
    private String homeTeamCode;
    private String awayTeamName;
    private String awayTeamCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupStage() {
        return groupStage;
    }

    public void setGroupStage(String groupStage) {
        this.groupStage = groupStage;
    }

    public String getKnockoutStage() {
        return knockoutStage;
    }

    public void setKnockoutStage(String knockoutStage) {
        this.knockoutStage = knockoutStage;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getHomeTeamCode() {
        return homeTeamCode;
    }

    public void setHomeTeamCode(String homeTeamCode) {
        this.homeTeamCode = homeTeamCode;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getAwayTeamCode() {
        return awayTeamCode;
    }

    public void setAwayTeamCode(String awayTeamCode) {
        this.awayTeamCode = awayTeamCode;
    }

    public String getScore() {
        if (score == null) {
            return null;
        }
        // SQL export corrupted en-dash separators as ???
        return score.replace("???", "-");
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public String getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public String getHomeTeamScoreMargin() {
        return homeTeamScoreMargin;
    }

    public void setHomeTeamScoreMargin(String homeTeamScoreMargin) {
        this.homeTeamScoreMargin = homeTeamScoreMargin;
    }

    public String getAwayTeamScoreMargin() {
        return awayTeamScoreMargin;
    }

    public void setAwayTeamScoreMargin(String awayTeamScoreMargin) {
        this.awayTeamScoreMargin = awayTeamScoreMargin;
    }

    public String getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(String extraTime) {
        this.extraTime = extraTime;
    }

    public String getPenaltyShootout() {
        return penaltyShootout;
    }

    public void setPenaltyShootout(String penaltyShootout) {
        this.penaltyShootout = penaltyShootout;
    }

    public String getScorePenalties() {
        return scorePenalties;
    }

    public void setScorePenalties(String scorePenalties) {
        this.scorePenalties = scorePenalties;
    }

    public String getHomeTeamScorePenalties() {
        return homeTeamScorePenalties;
    }

    public void setHomeTeamScorePenalties(String homeTeamScorePenalties) {
        this.homeTeamScorePenalties = homeTeamScorePenalties;
    }

    public String getAwayTeamScorePenalties() {
        return awayTeamScorePenalties;
    }

    public void setAwayTeamScorePenalties(String awayTeamScorePenalties) {
        this.awayTeamScorePenalties = awayTeamScorePenalties;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getHomeTeamWin() {
        return homeTeamWin;
    }

    public void setHomeTeamWin(String homeTeamWin) {
        this.homeTeamWin = homeTeamWin;
    }

    public String getAwayTeamWin() {
        return awayTeamWin;
    }

    public void setAwayTeamWin(String awayTeamWin) {
        this.awayTeamWin = awayTeamWin;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    private String score;
    private String homeTeamScore;
    private String awayTeamScore;
    private String homeTeamScoreMargin;
    private String awayTeamScoreMargin;
    private String extraTime;
    private String penaltyShootout;
    private String scorePenalties;
    private String homeTeamScorePenalties;
    private String awayTeamScorePenalties;
    private String result;
    private String homeTeamWin;
    private String awayTeamWin;
    private String draw;
    
    public HistoryMatchData(Map<String,String> mapParams) throws IllegalAccessException, NoSuchFieldException {
        try{
            for(Map.Entry<String,String> entry:mapParams.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                Field field = this.getClass().getDeclaredField(key);
                field.setAccessible(true);
                if(field.getType() == LocalDate.class){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                    LocalDate parse = LocalDate.parse(value, formatter);
                    field.set(this,parse);
                }
                else{
                    field.set(this,value);
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public HistoryMatchData(String tournamentId, String tournamentName, String matchId, String matchName, String stageName, String groupName, String groupStage, String knockoutStage, LocalDate matchDate, String stadiumId, String stadiumName, String cityName, String countryName, String homeTeamName, String homeTeamCode, String awayTeamName, String awayTeamCode, String score, String homeTeamScore, String awayTeamScore, String homeTeamScoreMargin, String awayTeamScoreMargin, String extraTime, String penaltyShootout, String scorePenalties, String homeTeamScorePenalties, String awayTeamScorePenalties, String result, String homeTeamWin, String awayTeamWin, String draw) {
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.matchId = matchId;
        this.matchName = matchName;
        this.stageName = stageName;
        this.groupName = groupName;
        this.groupStage = groupStage;
        this.knockoutStage = knockoutStage;
        this.matchDate = matchDate;
        this.stadiumId = stadiumId;
        this.stadiumName = stadiumName;
        this.cityName = cityName;
        this.countryName = countryName;
        this.homeTeamName = homeTeamName;
        this.homeTeamCode = homeTeamCode;
        this.awayTeamName = awayTeamName;
        this.awayTeamCode = awayTeamCode;
        this.score = score;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.homeTeamScoreMargin = homeTeamScoreMargin;
        this.awayTeamScoreMargin = awayTeamScoreMargin;
        this.extraTime = extraTime;
        this.penaltyShootout = penaltyShootout;
        this.scorePenalties = scorePenalties;
        this.homeTeamScorePenalties = homeTeamScorePenalties;
        this.awayTeamScorePenalties = awayTeamScorePenalties;
        this.result = result;
        this.homeTeamWin = homeTeamWin;
        this.awayTeamWin = awayTeamWin;
        this.draw = draw;
    }

    public HistoryMatchData() {
        
    }
}
