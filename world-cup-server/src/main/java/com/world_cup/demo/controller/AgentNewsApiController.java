package com.world_cup.demo.controller;

import com.world_cup.demo.dto.NewsSummaryDto;
import com.world_cup.demo.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent/newsapi")
public class AgentNewsApiController {

    private final NewsService newsService;

    public AgentNewsApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/generalNewsSummary")
    public ResponseEntity<NewsSummaryDto> getGeneralNewsSummary() {
        NewsSummaryDto summary = newsService.getGeneralNewsSummary();
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();
    }

    @GetMapping("/byTeamName")
    public ResponseEntity<NewsSummaryDto> getNewsSummaryByTeamName(@RequestParam String teamName) {
        NewsSummaryDto summary = newsService.getNewsSummaryByTeam(teamName);
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();
    }
}
