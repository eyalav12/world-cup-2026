package com.world_cup.demo.consumer;

import com.world_cup.demo.dto.BetToUpdate;
import com.world_cup.demo.service.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQChunkBetsConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQChunkBetsConsumer.class);
    private BetService betService;

    public RabbitMQChunkBetsConsumer(BetService betService){
        this.betService = betService;
    }

    @RabbitListener(queues = {"chunk_work_queue"},concurrency = "5")
    public void consume(BetToUpdate betToUpdate){
        logger.info("receive bet update chunk message "+betToUpdate.toString());
        betService.batchUpdate(betToUpdate);
    }
}
