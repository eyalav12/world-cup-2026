package com.world_cup.demo.controller;

import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.service.EspnNewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/espn/news")
public class EspnNewsController {

    private final EspnNewsService espnNewsService;

    public EspnNewsController(EspnNewsService espnNewsService) {
        this.espnNewsService = espnNewsService;
    }

    @GetMapping("/generalNews")
    public ResponseEntity<NewsResponse> getGeneralNewsResponse() {
        NewsResponse newsResponse = espnNewsService.getGeneralNews();
        return newsResponse != null ? ResponseEntity.ok(newsResponse) : ResponseEntity.noContent().build();
    }

    @GetMapping("/byTeamName")
    public ResponseEntity<NewsResponse> getNewsByTeamName(@RequestParam String teamName) {
        NewsResponse newsResponse = espnNewsService.getNewsByTeam(teamName);
        return newsResponse != null ? ResponseEntity.ok(newsResponse) : ResponseEntity.noContent().build();
    }
}
