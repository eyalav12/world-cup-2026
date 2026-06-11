package com.world_cup.demo.service;

import com.world_cup.demo.dto.PlayerDto;
import com.world_cup.demo.entities.Player;
import com.world_cup.demo.entities.Team;
import com.world_cup.demo.repositories.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamsService {
    private TeamRepository teamRepository;
    private static final Logger logger = LoggerFactory.getLogger(TeamsService.class);

    public TeamsService(TeamRepository teamRepository){
        this.teamRepository = teamRepository;
    }

    public List<String> getTeamsByGroupName(String groupName){
        try{
            List<Team> teamsByGroup = teamRepository.findByGroupId(groupName);
            return teamsByGroup.stream().map(team -> team.getTeamName()).toList();
        }
        catch(Exception e){
            logger.error("error in find teams by group name "+groupName,e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<String,List<String>> getTeamsByGroups(){
        try{
            List<Team> teams = teamRepository.findAll();
            Map<String, List<String>> teamByGroupsMap = teams.stream().collect(Collectors.groupingBy(
                    Team::getGroupId,
                    Collectors.mapping(Team::getTeamName, Collectors.toList())
            ));
            return teamByGroupsMap;
        }
        catch(Exception e){
            logger.error("cannot find teams by groups ",e.getMessage());
            return new HashMap<>();
        }
    }

    public List<PlayerDto> getTeamSquad(String teamName){
        try{
            List<Player> allPlayersByTeamName = teamRepository.findAllPlayersByTeamName(teamName);
            return allPlayersByTeamName.stream().map(player -> new PlayerDto(player.getName(), player.getPosition())).toList();
        }
        catch(Exception e){
            logger.error("error in get team squad",e.getMessage());
            return Collections.emptyList();
        }
    }


}
