package com.world_cup.demo.controller;

import com.world_cup.demo.dto.NewsSummaryDto;
import com.world_cup.demo.service.EspnNewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent/espn/news")
public class AgentEspnNewsController {

    private final EspnNewsService espnNewsService;

    public AgentEspnNewsController(EspnNewsService espnNewsService) {
        this.espnNewsService = espnNewsService;
    }

    @GetMapping("/generalNewsSummary")
    public ResponseEntity<NewsSummaryDto> getGeneralNewsSummary() {
        NewsSummaryDto summary = espnNewsService.getGeneralNewsSummary();
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();
    }

    @GetMapping("/byTeamName")
    public ResponseEntity<NewsSummaryDto> getNewsSummaryByTeamName(@RequestParam String teamName) {
        NewsSummaryDto summary = espnNewsService.getNewsSummaryByTeam(teamName);
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();
    }
}
