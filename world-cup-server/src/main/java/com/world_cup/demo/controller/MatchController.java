package com.world_cup.demo.controller;

import com.world_cup.demo.dto.MatchDto;
import com.world_cup.demo.dto.MatchLineupsDto;
import com.world_cup.demo.service.MatchService;
import com.world_cup.demo.service.LineupService;
import com.world_cup.demo.util.apiUtils.FootballDataApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/matches")
@RestController
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);
    private MatchService matchService;
    private FootballDataApiUtil footballDataApiUtilService;
    private LineupService lineupService;

    public MatchController(MatchService matchService, FootballDataApiUtil footballDataApiUtilService, LineupService lineupService){
        this.matchService = matchService;
        this.footballDataApiUtilService = footballDataApiUtilService;
        this.lineupService = lineupService;
    }

    @GetMapping("/byDate")
    public ResponseEntity<List<MatchDto>> getMatchesByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(matchService.getMatchesByDate(fromDate, toDate, status));
    }

    @GetMapping("/byTeamName")
    public ResponseEntity<List<MatchDto>> getMatchesByTeamName(
            @RequestParam String teamName,
            @RequestParam(required = false, defaultValue = "TIMED") String status,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(matchService.getMatchesByTeamName(teamName, status, limit));
    }

    @GetMapping("/byGroupName")
    public ResponseEntity<List<MatchDto>> getUpcomingMatchesByGroupName(
            @RequestParam String groupName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(matchService.getMatchesByGroupName(groupName, status, limit));
    }

    @GetMapping("/byMatchId")
    public ResponseEntity<MatchDto> getMatchByMatchId(@RequestParam Integer matchId) {
        MatchDto match = matchService.getMatchByMatchId(matchId);
        return match != null ? ResponseEntity.ok(match) : ResponseEntity.noContent().build();
    }

    @GetMapping("/recent")
    public ResponseEntity<List<MatchDto>> getRecentFinishedMatches(
            @RequestParam(defaultValue = "10") int limit) {
        List<MatchDto> matches = matchService.getRecentFinishedMatches(limit);
        return matches.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(matches);
    }

    @GetMapping("/lineups")
    public ResponseEntity<MatchLineupsDto> getMatchLineups(@RequestParam Integer matchId) {
        MatchLineupsDto lineups = lineupService.getLineups(matchId);
        return lineups != null ? ResponseEntity.ok(lineups) : ResponseEntity.noContent().build();
    }
}
