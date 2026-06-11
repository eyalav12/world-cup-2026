package com.world_cup.demo.dto;

public class BetUpdateItem {
    private Long id;
    private String prediction;

    public BetUpdateItem(Long id, String prediction) {
        this.id = id;
        this.prediction = prediction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public BetUpdateItem() {

    }
}
