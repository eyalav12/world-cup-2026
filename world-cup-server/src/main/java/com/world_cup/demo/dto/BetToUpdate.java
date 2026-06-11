package com.world_cup.demo.dto;

import com.world_cup.demo.entities.Bet;

import java.util.List;

public class BetToUpdate {

    public BetToUpdate(String result, List<BetUpdateItem> betList) {
        this.result = result;
        this.betList = betList;
    }
    public BetToUpdate(){

    }

    private String result;
    private List<BetUpdateItem> betList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<BetUpdateItem> getBetList() {
        return betList;
    }

    public void setBatchOfGamesId(List<BetUpdateItem> betList) {
        this.betList = betList;
    }


    @Override
    public String toString() {
        return "BetToUpdate{" +
                "result='" + getResult() + '\'' +
                ", batchOfGamesId=" + getBetList() +
                '}';
    }
}
