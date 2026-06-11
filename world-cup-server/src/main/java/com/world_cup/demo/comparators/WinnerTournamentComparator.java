package com.world_cup.demo.comparators;

import com.world_cup.demo.dto.CleanedMarket;

import java.util.Comparator;

public class WinnerTournamentComparator implements Comparator<CleanedMarket> {


    @Override
    public int compare(CleanedMarket m1, CleanedMarket m2) {
        Double m1ToWin = m1.getOutcomePrices().get(0);
        Double m2ToWin = m2.getOutcomePrices().get(0);
        if(m1ToWin != m2ToWin){
            return Double.compare(m2ToWin,m1ToWin);
        }
        return m1.getQuestion().compareTo(m2.getQuestion());

    }
}
