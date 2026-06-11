package com.world_cup.demo.controller;

import com.world_cup.demo.dto.OddsSummaryDTO;
import com.world_cup.demo.service.OddsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/odds")
@RestController
public class OddsController {

    private OddsService oddsService;

    public OddsController(OddsService oddsService){
        this.oddsService = oddsService;
    }

    @GetMapping("/oddsSummaryByMatchId")
    public ResponseEntity<OddsSummaryDTO> getOddsSummaryByMatchId(@RequestParam Integer matchId){
        OddsSummaryDTO summary = oddsService.getOddsSummaryByTeamId(matchId);
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();
    }

    @GetMapping("/oddsSummaryByTeamNames")
    public ResponseEntity<OddsSummaryDTO> getOddsSummaryByTeamNames(
            @RequestParam String teamA,
            @RequestParam String teamB,
            @RequestParam(required = false) String matchDate
    ) {
        OddsSummaryDTO summary = oddsService.getOddsSummaryByTeamNames(teamA, teamB, matchDate);
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();
    }
}

