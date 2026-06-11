package com.world_cup.demo.controller;

import com.world_cup.demo.entities.GroupStandings;
import com.world_cup.demo.service.StandingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/standings")
public class StandingsController {

    private StandingsService standingsService;

    public StandingsController(StandingsService standingsService){
        this.standingsService = standingsService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<GroupStandings>> getAllStandings(){
        List<GroupStandings> allStandings = standingsService.getAllStandings();
        return allStandings != null? ResponseEntity.ok(allStandings):
                ResponseEntity.noContent().build();
    }

    @GetMapping("/byGroup")
    public ResponseEntity<List<GroupStandings>> getStandingsByGroup(@RequestParam String group){
        List<GroupStandings> standingsBtGroup = standingsService.getStandingsByGroup(group);
        return standingsBtGroup != null? ResponseEntity.ok(standingsBtGroup):
                ResponseEntity.noContent().build();
    }
}
