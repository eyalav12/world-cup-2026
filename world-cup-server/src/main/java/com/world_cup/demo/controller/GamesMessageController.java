package com.world_cup.demo.controller;

import com.world_cup.demo.dto.FinishedGame;
import com.world_cup.demo.publisher.RabbitMQProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GamesMessageController {

    private RabbitMQProducer rabbitMQProducer;

    public GamesMessageController(RabbitMQProducer rabbitMQProducer){
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @PostMapping("/public")
    public ResponseEntity<String> sendGameMessage(@RequestBody FinishedGame game){
        rabbitMQProducer.sendMessage(game);
        return ResponseEntity.ok("game message sent successfully");
    }
}
