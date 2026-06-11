package com.world_cup.demo.entities;

import jakarta.persistence.*;

@Entity
public class GroupStandings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    public String groupName;

    public Integer position;

    public Integer teamId;

    public String name;

    public String crest;

    public Integer playedGames;

    @Column(length = 32)
    public String form;

    public Integer won;

    public Integer draw;

    public Integer lost;

    public Integer points;

    public Integer goalsFor;

    public Integer goalsAgainst;

    public Integer goalsDifference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCrest() {
        return crest;
    }

    public void setCrest(String crest) {
        this.crest = crest;
    }

    public Integer getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(Integer playedGames) {
        this.playedGames = playedGames;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Integer getWon() {
        return won;
    }

    public void setWon(Integer won) {
        this.won = won;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getLost() {
        return lost;
    }

    public void setLost(Integer lost) {
        this.lost = lost;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(Integer goalsFor) {
        this.goalsFor = goalsFor;
    }

    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(Integer goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public Integer getGoalsDifference() {
        return goalsDifference;
    }

    public void setGoalsDifference(Integer goalsDifference) {
        this.goalsDifference = goalsDifference;
    }

    public GroupStandings(String groupName, Integer position, Integer teamId, String name, String crest, Integer playedGames, String form, Integer won, Integer draw, Integer lost, Integer points, Integer goalsFor, Integer goalsAgainst, Integer goalsDifference) {
        this.groupName = groupName;
        this.position = position;
        this.teamId = teamId;
        this.name = name;
        this.crest = crest;
        this.playedGames = playedGames;
        this.form = form;
        this.won = won;
        this.draw = draw;
        this.lost = lost;
        this.points = points;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalsDifference = goalsDifference;
    }

    public GroupStandings() {
    }
}
