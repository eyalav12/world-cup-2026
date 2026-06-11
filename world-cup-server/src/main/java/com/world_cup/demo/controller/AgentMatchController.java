package com.world_cup.demo.controller;

import com.world_cup.demo.dto.MatchLineupSummaryDto;
import com.world_cup.demo.service.LineupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent/matches")
public class AgentMatchController {

    private final LineupService lineupService;

    public AgentMatchController(LineupService lineupService) {
        this.lineupService = lineupService;
    }

    @GetMapping("/lineups")
    public ResponseEntity<MatchLineupSummaryDto> getMatchLineupSummary(@RequestParam Integer matchId) {
        MatchLineupSummaryDto summary = lineupService.getLineupSummary(matchId);
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();
    }
}
