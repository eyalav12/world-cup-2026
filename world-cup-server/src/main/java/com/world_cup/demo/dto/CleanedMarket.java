package com.world_cup.demo.dto;

import java.util.List;

public class CleanedMarket {
    public CleanedMarket(String question,List<Double> outcomePrices){
        this.question = question;
        this.outcomePrices = outcomePrices;
    }
    private String question;
    private List<Double> outcomePrices;

    public String getQuestion(){
        return question;
    }
    private void setQuestion(String question){
        this.question = question;
    }
    public List<Double> getOutcomePrices(){
        return outcomePrices;
    }
    private void setOutcomePrices(List<Double> rawOutcomePrices){
        this.outcomePrices = outcomePrices;
    }

    public static List<Double> transformRawStringToDoubleList(String rawPrices) {
        if (rawPrices == null || rawPrices.isBlank()) {
            return List.of(0.0,0.0);
        }

        String cleanString = rawPrices.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .trim();
        String[] priceArray = cleanString.split(",");
        double yesPrice = Double.parseDouble(priceArray[0]);
        double noPrice = Double.parseDouble(priceArray[1]);
        double percentageYes = yesPrice * 100;
        double percentageNo = noPrice * 100;

        return List.of(percentageYes,percentageNo);
    }

}
