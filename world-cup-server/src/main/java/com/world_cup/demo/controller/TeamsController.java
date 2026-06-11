package com.world_cup.demo.controller;

import com.world_cup.demo.dto.PlayerDto;
import com.world_cup.demo.service.TeamsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/teams")
@RestController
public class TeamsController {

    /*
        todo
        get data on a team/teams/group
        get history data on them so far
        get their next matches
        can be with redis too - it not changes much
     */
    private TeamsService teamsService;

    public TeamsController(TeamsService teamsService){
        this.teamsService = teamsService;
    }

    @GetMapping("/getTeamsByGroupName")
    public ResponseEntity<List<String>> getTeamsByGroup(@RequestParam String groupName){
        return ResponseEntity.ok(teamsService.getTeamsByGroupName(groupName));
    }

    @GetMapping("/getTeamsByGroups")
    public ResponseEntity<Map<String,List<String>>> getTeamsByGroups(){
        return ResponseEntity.ok(teamsService.getTeamsByGroups());
    }

    @GetMapping("/getTeamSquad")
    public ResponseEntity<List<PlayerDto>> getTeamSquad(@RequestParam String teamName){
        return ResponseEntity.ok(teamsService.getTeamSquad(teamName));
    }

}
