package com.world_cup.demo.scheduledTask;

import com.world_cup.demo.client.FootballDataClient;
import com.world_cup.demo.dto.FinishedGame;
import com.world_cup.demo.publisher.RabbitMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SchedulesTaskEndedGames {

    private final RedisTemplate<String,Object> redisTemplate;
    private final RabbitMQProducer rabbitMQProducer;
    private final FootballDataClient footballDataClient;
    private static final Logger logger = LoggerFactory.getLogger(SchedulesTaskEndedGames.class);

    public SchedulesTaskEndedGames(
            RedisTemplate<String,Object> redisTemplate,
            RabbitMQProducer rabbitMQProducer,
            FootballDataClient footballDataClient
    ) {
        this.redisTemplate = redisTemplate;
        this.rabbitMQProducer = rabbitMQProducer;
        this.footballDataClient = footballDataClient;
    }

    @Async
    @Scheduled(fixedRate = 1000*60*60)
    public void getFinishedGamesOfDay(){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayString = today.format(formatter);
        String yesterdayString = yesterday.format(formatter);
        System.out.println("hi from scheduler");
        String response = footballDataClient.fetchCompetitionFinishedMatches("CL", yesterdayString, todayString);
        if(response.isEmpty()){
            return;
        }
        List<FinishedGame> finishedGames = buildListOfFinishedMatchesResult(response);
        for(FinishedGame finishedGame:finishedGames){
            try{
                Boolean matchesFinished = redisTemplate.opsForSet().isMember("matches_finished", finishedGame.getId());
                if(!matchesFinished){
                    Long matchesFinished1 = redisTemplate.opsForSet().add("matches_finished", finishedGame.getId());
                    rabbitMQProducer.sendMessage(finishedGame);
                }
            }
            catch(Exception e){
                logger.info(e.getMessage());
            }

        }

    }

    private List<FinishedGame> buildListOfFinishedMatchesResult(String body){
        ObjectMapper objectMapper=new ObjectMapper();
        Map map = objectMapper.readValue(body, Map.class);
        Map<String,List<String>> matchesMap = new HashMap<>();
        List<FinishedGame> finishedGames = new ArrayList<>();
        List<Map> matches =(List<Map>) map.getOrDefault("matches", Collections.emptyList());
        for(Map m:matches){
//            Map home = (Map)m.get("homeTeam");
//            Map away = (Map)m.get("awayTeam");
//            String homeName = (String)home.get("name");
//            String awayName = (String)away.get("name");
//            String stage = (String)m.get("stage");
//            Map score = (Map) m.get("score");
//            Map fullTimeRes = (Map)score.get("fullTime");
//            Integer homeRes= (Integer) fullTimeRes.get("home");
//            Integer awayRes= (Integer) fullTimeRes.get("away");
//            String result = (String)score.get("winner");
//            StringBuilder key = new StringBuilder();
//            key.append(homeName).append(":").append(awayName).append(":").append(stage);
//            List<String> resultArray = new ArrayList<>();
//            String fullRes = homeRes+"-"+awayRes;
//            resultArray.add(result);
//            resultArray.add(fullRes);
//            matchesMap.put(key.toString(),resultArray);
            Integer id = (Integer) m.get("id");
            String result = (String) ((Map)m.get("score")).get("winner");
            FinishedGame finishedGame = new FinishedGame(id, result);
            finishedGames.add(finishedGame);
        }
        return finishedGames;
    }
}
