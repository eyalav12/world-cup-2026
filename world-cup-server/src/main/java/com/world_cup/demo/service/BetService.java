package com.world_cup.demo.service;

import com.world_cup.demo.dto.BetToUpdate;
import com.world_cup.demo.dto.FinishedGame;
import com.world_cup.demo.entities.Bet;
import com.world_cup.demo.repositories.BetBatchRepository;
import com.world_cup.demo.repositories.BetRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class BetService {
    private BetRepository betRepository;
    private BetBatchRepository betBatchRepository;

    public BetService(BetRepository betRepository,BetBatchRepository betBatchRepository){
        this.betRepository = betRepository;
        this.betBatchRepository = betBatchRepository;
    }


    public void batchUpdate(BetToUpdate betToUpdate){
        try{
            betBatchRepository.batchUpdate(betToUpdate.getBetList(),betToUpdate.getResult());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void batchUpdateDemo(Integer gameId,String result){
        try{
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            List<Bet> allBetsByGameId = betRepository.findAllBetsByGameId(gameId);
            List<List<Bet>> batchesList = splitListToBatches(allBetsByGameId);
            for(List<Bet> batch:batchesList){
                executorService.submit(()->betBatchRepository.batchUpdate(batch,result,gameId));
            }
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void batchUpdateTest(Integer gameId,String result){
        try{
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            List<Bet> allBetsByGameId = betRepository.findAllBetsByGameId(gameId);
            List<List<Bet>> batchesList = splitListToBatches(allBetsByGameId);
            for(List<Bet> batch:batchesList){
                executorService.submit(()->betBatchRepository.batchUpdate(batch,result,gameId));
            }
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<List<Bet>> getBatchesBetsListFromGameEndMessage(FinishedGame finishedGame){
        try{
            String result = finishedGame.getResult();
            Integer gameId = finishedGame.getId();
            List<Bet> allBetsByGameId = betRepository.findAllBetsByGameId(gameId);
            List<List<Bet>> batches = splitListToBatches(allBetsByGameId);
            return batches;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }

    }

    public List<List<Bet>> splitListToBatches(List<Bet> betList){
        int size = 50;
        List<Bet> currentBatch = new ArrayList<>();
        List<List<Bet>> batchesList = new ArrayList<>();
        int index = 0;
        while(index<betList.size()){
            while(index < betList.size() && currentBatch.size()<size){
                Bet currentBet = betList.get(index);
                currentBatch.add(currentBet);
                index+=1;
            }
            batchesList.add(new ArrayList<>(currentBatch));
            currentBatch = new ArrayList<>();
        }
        return batchesList;
    }
}
