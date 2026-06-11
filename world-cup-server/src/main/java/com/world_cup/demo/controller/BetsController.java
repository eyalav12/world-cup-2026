package com.world_cup.demo.controller;

import com.world_cup.demo.service.BetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bets")
public class BetsController {
    private BetService betService;
    public BetsController(BetService betService){
        this.betService = betService;
    }

    /*TODO add bet, get bet, delete bet, update bet, get all bets. get all also paging.
           get by name, get by id, get by date...
           add bet, different stages diffrent score...
           take jwt and assign to the matched user.
           excpetion of user not found error
           redis for get the bets of user- it not changing much
           special bets like winner of tournament, best goaler and so
     */


    @GetMapping("/batch")
    public ResponseEntity<String> batchUpdate(@RequestParam Integer gamedId, @RequestParam String result){
        betService.batchUpdateTest(gamedId,result);
        return ResponseEntity.ok("done batch update...");
    }
}
