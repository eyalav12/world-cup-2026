package com.world_cup.demo.util.apiUtils;

import com.world_cup.demo.client.NewsApiDataClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class NewsDataApiUtil {
    private final NewsApiDataClient newsApiDataClient;
    private final ObjectMapper objectMapper;

    public NewsDataApiUtil(NewsApiDataClient newsApiDataClient, ObjectMapper objectMapper){
        this.newsApiDataClient = newsApiDataClient;
        this.objectMapper = objectMapper;
    }

    public String httpCallToApiForGeneralNews(){
        return newsApiDataClient.fetchGeneralNews();
    }

    public String httpCallToApiForSpecificTeamNews(String teamName){
        return newsApiDataClient.fetchNewsByTeamName(teamName);
    }
}
