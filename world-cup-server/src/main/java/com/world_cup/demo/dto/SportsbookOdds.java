package com.world_cup.demo.dto;

public class SportsbookOdds {
    private String bookmaker;
    private int homeOdds;
    private double homePct;
    private int drawOdds;
    private double drawPct;
    private int awayOdds;
    private double awayPct;

    // Default constructor for Jackson
    public SportsbookOdds() {}

    public SportsbookOdds(String bookmaker, int homeOdds, double homePct, int drawOdds, double drawPct, int awayOdds, double awayPct) {
        this.bookmaker = bookmaker;
        this.homeOdds = homeOdds;
        this.homePct = homePct;
        this.drawOdds = drawOdds;
        this.drawPct = drawPct;
        this.awayOdds = awayOdds;
        this.awayPct = awayPct;
    }

    // Getters and Setters
    public String getBookmaker() { return bookmaker; }
    public void setBookmaker(String bookmaker) { this.bookmaker = bookmaker; }
    public int getHomeOdds() { return homeOdds; }
    public void setHomeOdds(int homeOdds) { this.homeOdds = homeOdds; }
    public double getHomePct() { return homePct; }
    public void setHomePct(double homePct) { this.homePct = homePct; }
    public int getDrawOdds() { return drawOdds; }
    public void setDrawOdds(int drawOdds) { this.drawOdds = drawOdds; }
    public double getDrawPct() { return drawPct; }
    public void setDrawPct(double drawPct) { this.drawPct = drawPct; }
    public int getAwayOdds() { return awayOdds; }
    public void setAwayOdds(int awayOdds) { this.awayOdds = awayOdds; }
    public double getAwayPct() { return awayPct; }
    public void setAwayPct(double awayPct) { this.awayPct = awayPct; }
}
