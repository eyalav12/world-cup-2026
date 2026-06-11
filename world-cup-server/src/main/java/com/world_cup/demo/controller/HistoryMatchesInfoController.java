package com.world_cup.demo.controller;

import com.world_cup.demo.dto.MatchDto;
import com.world_cup.demo.dto.MatchResultsContainer;
import com.world_cup.demo.entities.HistoryMatchData;
import com.world_cup.demo.service.HistoryMatchesInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history/data")
public class HistoryMatchesInfoController {
    private HistoryMatchesInfoService historyMatchesInfoService;

    public HistoryMatchesInfoController(HistoryMatchesInfoService historyMatchesInfoService){
        this.historyMatchesInfoService = historyMatchesInfoService;
    }

    @GetMapping("/teamLastMatches")
    public List<HistoryMatchData> getTeamLastMatchesFromDB(@RequestParam String teamName){
        return historyMatchesInfoService.getTeamLastMatches(teamName);
    }

    @GetMapping("/teamsLastMatches")
    public MatchResultsContainer<HistoryMatchData> getTeamsLastMatchesFromDB(@RequestParam String teamA,@RequestParam String teamB){
        System.out.println("inside getTeamsLastMatchesFromDB");
        List<HistoryMatchData> teamALastMatches = historyMatchesInfoService.getTeamLastMatches(teamA);
        List<HistoryMatchData> teamBLastMatches = historyMatchesInfoService.getTeamLastMatches(teamB);
        return new MatchResultsContainer<>(teamALastMatches,teamBLastMatches);

    }

    @GetMapping("/headToHead")
    public List<HistoryMatchData> getHeadToHeadLastMatchesFromDB(@RequestParam String teamA, @RequestParam String teamB){
        System.out.println("inside getHeadToHeadLastMatchesFromDB");
        return historyMatchesInfoService.getHeadToHead(teamA,teamB);
    }

    @GetMapping("/teamsLastMatchesApi")
    public MatchResultsContainer<MatchDto> getTeamsLastMatchesFromApi(@RequestParam String teamA, @RequestParam String teamB){
        System.out.println("inside getTeamsLastMatchesFromApi");
        return historyMatchesInfoService.getTeamsLastMatchesFromApi(teamA,teamB);
    }

    @GetMapping("/teamLastMatchesApi")
    public List<MatchDto> getTeamLastMatchesFromApi(
            @RequestParam String teamName,
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        return historyMatchesInfoService.getTeamLastMatchesFromApi(teamName, limit);
    }
}
