package com.world_cup.demo.service.startupservices;


import com.world_cup.demo.client.FootballDataClient;
import com.world_cup.demo.entities.Player;
import com.world_cup.demo.entities.Team;
import com.world_cup.demo.repositories.PlayerRepository;
import com.world_cup.demo.repositories.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StartUpGetTeamsAndPlayersCommandRunner implements CommandLineRunner {
    private RedisTemplate redisTemplate;
    private TeamRepository teamRepository;
    private PlayerRepository playerRepository;
    private FootballDataClient footballDataClient;
    private Logger logger = LoggerFactory.getLogger(StartUpGetTeamsAndPlayersCommandRunner.class);

    public StartUpGetTeamsAndPlayersCommandRunner(
            RedisTemplate redisTemplate,
            TeamRepository teamRepository,
            PlayerRepository playerRepository,
            FootballDataClient footballDataClient
    ) {
        this.redisTemplate = redisTemplate;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.footballDataClient = footballDataClient;
    }

    @Override
    public void run(String... args) throws Exception {
        try{
            if(!teamRepository.findAll().isEmpty()){
                return;
            }
            String teamsResponse = footballDataClient.fetchWorldCupResource("teams");
            String standingsResponse = footballDataClient.fetchWorldCupResource("standings");
            Map<Integer,String> teamsByGroup = getTeamsByGroup(standingsResponse);
            logger.info(teamsResponse);
            List<Player> players = convertResponseToPlayersObj(teamsResponse);
            List<Team> teams = convertResponseToTeamsObj(teamsResponse,teamsByGroup);
            saveTeamsToDB(teams);
            savePlayersToDB(players);
        }
        catch(Exception e){
            logger.info(e.getMessage());
        }
    }

    private void savePlayersToDB(List<Player> playersList){
        for(Player player:playersList){
            playerRepository.save(player);
        }
    }

    private void saveTeamsToDB(List<Team> teamsList){
        for(Team team:teamsList){
            teamRepository.save(team);
            redisTemplate.opsForValue().set(team.getTeamName(),team.getTeamId());
        }
    }

    private List<Player> convertResponseToPlayersObj(String response){
        ObjectMapper objectMapper=new ObjectMapper();
        Map teamsMap = objectMapper.readValue(response, Map.class);
        List<Map> teams = (List<Map>) teamsMap.get("teams");
        List<Player> players = new ArrayList<>();
        for(Map team:teams){
            Integer teamId = (Integer) team.get("id");
            List<Map> squad = (List<Map>) team.get("squad");
            for(Map player:squad){
                Integer playerId = (Integer) player.get("id");
                String playerName = (String) player.get("name");
                String playerPosition = (String) player.get("position");
                players.add(new Player(playerId,playerName,playerPosition,teamId));
            }
        }
        return players;
    }

    private Map<Integer,String> getTeamsByGroup(String response){
        ObjectMapper objectMapper=new ObjectMapper();
        Map standingssMap = objectMapper.readValue(response, Map.class);
        List<Map> standingsList = (List<Map>) standingssMap.get("standings");
        Map<Integer,String> teamsToGroupMap = new HashMap<>();
        for(Map group:standingsList){
            String groupName =(String) group.get("group");
            List <Map> groupTableList = (List) group.get("table");
            for(Map teamMap:groupTableList){
                Map teamMapNested = (Map)teamMap.get("team");
                Integer teamId = (Integer)teamMapNested.get("id");
                teamsToGroupMap.put(teamId,groupName);
            }
        }
        return teamsToGroupMap;
    }

    private List<Team> convertResponseToTeamsObj(String response,Map<Integer,String> teamsToGroupMap){
        ObjectMapper objectMapper=new ObjectMapper();
        Map teamsMap = objectMapper.readValue(response, Map.class);
        List<Map> teams = (List<Map>) teamsMap.get("teams");
        List<Team> teamsList = teams.stream().map(map1 -> new Team((String) map1.get("name"), (Integer) map1.get("id"),teamsToGroupMap.get((Integer) map1.get("id")))).toList();
        return teamsList;
    }
}
