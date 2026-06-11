package com.world_cup.demo.controller;

import com.world_cup.demo.dto.CleanedPolymarketOdds;
import com.world_cup.demo.service.PolymarketOddsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/polymarketodds")
public class PolymarketOddsController {

    private final PolymarketOddsService polymarketOddsService;

    public PolymarketOddsController(PolymarketOddsService polymarketOddsService){
        this.polymarketOddsService = polymarketOddsService;
    }

    @GetMapping("/tournamentWinnerOdds")
    public ResponseEntity<CleanedPolymarketOdds> getTournamentWinnerOdds(){
        CleanedPolymarketOdds tournamentWinnerOdds = polymarketOddsService.getTournamentWinnerOdds();
        return tournamentWinnerOdds != null? ResponseEntity.ok(tournamentWinnerOdds):
                ResponseEntity.noContent().build();
    }

    @GetMapping("/topScorerOdds")
    public ResponseEntity<CleanedPolymarketOdds> getTopScorerOdds(){
        CleanedPolymarketOdds tournamentWinnerOdds = polymarketOddsService.getTopScorerOdds();
        return tournamentWinnerOdds != null? ResponseEntity.ok(tournamentWinnerOdds):
                ResponseEntity.noContent().build();
    }

    @GetMapping("/groupWinnerOdds")
    public ResponseEntity<CleanedPolymarketOdds> getGroupWinnerOdds(@RequestParam String groupName){
        CleanedPolymarketOdds groupWinnerOdds = polymarketOddsService.getGroupWinnerOdds(groupName);
        return groupWinnerOdds != null? ResponseEntity.ok(groupWinnerOdds):
                ResponseEntity.noContent().build();
    }

    @GetMapping("/advancementOdds")
    public ResponseEntity<CleanedPolymarketOdds> getAdvancementOdds(@RequestParam String stage){
        CleanedPolymarketOdds advancementOdds = polymarketOddsService.getAdvancementOdds(stage);
        return advancementOdds != null ? ResponseEntity.ok(advancementOdds) :
                ResponseEntity.noContent().build();
    }

}
