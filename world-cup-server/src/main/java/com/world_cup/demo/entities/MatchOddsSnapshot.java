package com.world_cup.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** Last known bookmaker odds for a match — kept after the fixture finishes. */
@Entity
@Table(name = "match_odds_snapshot")
public class MatchOddsSnapshot {

    @Id
    private Integer matchId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summaryJson;

    private Instant capturedAt;

    protected MatchOddsSnapshot() {}

    public MatchOddsSnapshot(Integer matchId, String summaryJson, Instant capturedAt) {
        this.matchId = matchId;
        this.summaryJson = summaryJson;
        this.capturedAt = capturedAt;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public String getSummaryJson() {
        return summaryJson;
    }

    public void setSummaryJson(String summaryJson) {
        this.summaryJson = summaryJson;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }
}
