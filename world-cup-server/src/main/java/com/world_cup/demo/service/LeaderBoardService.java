package com.world_cup.demo.service;

import com.world_cup.demo.dto.UserLeaderBoardDto;
import com.world_cup.demo.entities.User;
import com.world_cup.demo.repositories.BetBatchRepository;
import com.world_cup.demo.repositories.LeaderBoardRepository;
import com.world_cup.demo.service.cache.LeaderBoardCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LeaderBoardService {

    private LeaderBoardRepository leaderBoardRepository;
    private LeaderBoardCache leaderBoardCache;
    private static final Logger logger = LoggerFactory.getLogger(LeaderBoardService.class);

    public LeaderBoardService(LeaderBoardRepository leaderBoardRepository,LeaderBoardCache leaderBoardCache){
        this.leaderBoardCache = leaderBoardCache;
        this.leaderBoardRepository = leaderBoardRepository;
    }

    public List<UserLeaderBoardDto> getGlobalLeaderBoard(Pageable pageRequest){
        try{
            List<UserLeaderBoardDto> globalLeaderBoardPage = leaderBoardRepository.getGlobalLeaderBoardPage(pageRequest.getPageSize(), pageRequest.getPageNumber());
            return globalLeaderBoardPage;
        }
        catch(Exception e){
            logger.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
