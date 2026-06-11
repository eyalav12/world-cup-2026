package com.world_cup.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
public class Bet {
    public enum Level{
        GROUPS,
        SIXTEEN,
        QUARTERFINAL,
        SEMIFINAL,
        FINAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String homeTeam;
    private Integer gameId;
    private String status;
    private LocalDate date;

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    private String stage;

    public Bet(String homeTeam, Integer gameId, String status, LocalDate date, Level level, String exactPrediction, String prediction, String awayTeam, String result, User user) {
        this.homeTeam = homeTeam;
        this.gameId = gameId;
        this.status = status;
        this.date = date;
        this.level = level;
        this.exactPrediction = exactPrediction;
        this.prediction = prediction;
        this.awayTeam = awayTeam;
        this.result = result;
        this.user = user;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getExactPrediction() {
        return exactPrediction;
    }

    public void setExactPrediction(String exactPrediction) {
        this.exactPrediction = exactPrediction;
    }

    private Level level;
    private String exactPrediction;


    public String getPrediction() {
        return prediction;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    private String prediction;

    public Bet(Long id, String homeTeam, String awayTeam, String result, User user,Integer gamedId,String prediction,String status) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.result = result;
        this.user = user;
        this.gameId = gamedId;
        this.prediction = prediction;
        this.status = status;
    }
    public Bet(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGameId(){
        return gameId;
    }

    public void setGameId(Integer gameId){
        this.gameId = gameId;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private String awayTeam;
    private String result;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;
}
