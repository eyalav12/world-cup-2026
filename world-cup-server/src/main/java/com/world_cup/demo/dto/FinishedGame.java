package com.world_cup.demo.dto;

import lombok.Data;


public class FinishedGame {
    private Integer id;

    public FinishedGame() {
    }

    public FinishedGame(Integer id, String result) {
        this.id = id;
        this.result = result;
    }

    public Integer getId() {
        return id;
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

    private String result;

    @Override
    public String toString(){
        return "Game "+"("+"id="+getId()+", "+ "result="+getResult()+")";
    }
}
