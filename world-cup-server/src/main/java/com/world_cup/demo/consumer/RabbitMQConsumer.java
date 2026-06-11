package com.world_cup.demo.consumer;

import com.world_cup.demo.dto.BetToUpdate;
import com.world_cup.demo.dto.BetUpdateItem;
import com.world_cup.demo.dto.FinishedGame;
import com.world_cup.demo.entities.Bet;
import com.world_cup.demo.publisher.RabbitMQProducer;
import com.world_cup.demo.repositories.BetRepository;
import com.world_cup.demo.service.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RabbitMQConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final BetService betService;
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.gamesexchange.name}")
    private String exchange;

    @Value("${rabbitmq.gamesroutekey.name}")
    private String routeKey;

    public RabbitMQConsumer(BetService betService,RabbitTemplate rabbitTemplate){
        this.betService = betService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"${rabbitmq.gamesqueue.name}"})
    public void consume(FinishedGame game){
        //consume message of end game
        logger.info("message received -> "+game.toString());
        sendBetBatchMessages(game);
    }

    private void sendBetBatchMessage(BetToUpdate betToUpdate){
        rabbitTemplate.convertAndSend(exchange,"chunk_work_key",betToUpdate);
    }

    private void sendBetBatchMessages(FinishedGame finishedGame){
        List<BetToUpdate> betsToUpdates = fetchBetsToUpdate(finishedGame);
        for(BetToUpdate betToUpdate:betsToUpdates){
            sendBetBatchMessage(betToUpdate);
        }
    }

    private List<BetToUpdate> fetchBetsToUpdate(FinishedGame finishedGame){
        String result = finishedGame.getResult();
        Integer gameId = finishedGame.getId();
        List<List<Bet>> batchesBetsListFromGameEndMessage = betService.getBatchesBetsListFromGameEndMessage(finishedGame);
        List<BetToUpdate> betToUpdates = batchesBetsListFromGameEndMessage.stream().map(betList -> new BetToUpdate(result, betList.stream().map(bet -> new BetUpdateItem(bet.getId(),bet.getPrediction())).toList())).toList();
        return betToUpdates;
    }

}
