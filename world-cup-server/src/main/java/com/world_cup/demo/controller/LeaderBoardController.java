package com.world_cup.demo.controller;

import com.world_cup.demo.dto.UserLeaderBoardDto;
import com.world_cup.demo.service.LeaderBoardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderBoard")

public class LeaderBoardController {
    private LeaderBoardService leaderBoardService;

    public LeaderBoardController(LeaderBoardService leaderBoardService){
        this.leaderBoardService = leaderBoardService;
    }
    /*
        todo
        first step sql for compute score of users
        group leader and global leaders
        pagination for many global leader boards
        redis cache for it also - delete when score updates
        next step - sql db table for leader boards
        leader board by few options - of the week/month, ranking jump indication with sql
     */
    @GetMapping("/global")
    public ResponseEntity<List<UserLeaderBoardDto>> getGlobalLeaderBoard(Pageable pageRequest){
        return ResponseEntity.ok(leaderBoardService.getGlobalLeaderBoard(pageRequest));
    }

    @GetMapping("/group")
    public ResponseEntity<List<UserLeaderBoardDto>> getGroupLeaderBoard(Pageable pageRequest, @RequestParam String group){
        return ResponseEntity.ok(leaderBoardService.getGlobalLeaderBoard(pageRequest));
    }


}
