package com.world_cup.demo.dto;

public class UserLeaderBoardDto {
    private String name;
    private Integer totalScore;

    public UserLeaderBoardDto(String name, Integer totalScore) {
        this.name = name;
        this.totalScore = totalScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
}
