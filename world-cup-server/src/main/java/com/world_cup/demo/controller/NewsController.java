package com.world_cup.demo.controller;

import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;
    private final Logger logger = LoggerFactory.getLogger(NewsController.class);

    public NewsController(NewsService newsService){
        this.newsService = newsService;
    }

    @GetMapping("/generalNews")
    public ResponseEntity<NewsResponse> getGeneralNewsResponse(){
        NewsResponse newsResponse = newsService.getGeneralNews();
        return newsResponse != null? ResponseEntity.ok(newsResponse):
                ResponseEntity.noContent().build();
    }

    @GetMapping("byTeamName")
    public ResponseEntity<NewsResponse> getNewsByTeamName(@RequestParam String teamName){
        NewsResponse newsResponse = newsService.getNewsByTeam(teamName);
        return newsResponse !=null ?ResponseEntity.ok(newsResponse):
                ResponseEntity.noContent().build();
    }
}
