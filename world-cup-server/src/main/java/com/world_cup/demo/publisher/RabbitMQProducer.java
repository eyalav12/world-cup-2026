package com.world_cup.demo.publisher;

import com.world_cup.demo.dto.FinishedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);


    @Value("${rabbitmq.gamesexchange.name}")
    private String exchange;

    @Value("${rabbitmq.gamesroutekey.name}")
    private String routeKey;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(FinishedGame game){
        logger.info("message was sent "+game.toString());
        rabbitTemplate.convertAndSend(exchange,routeKey,game);

    }
}
