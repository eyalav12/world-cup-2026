package com.world_cup.demo.controller;

import com.world_cup.demo.dto.BetRequest;
import com.world_cup.demo.entities.Bet;
import com.world_cup.demo.entities.Person;
import com.world_cup.demo.entities.User;
import com.world_cup.demo.repositories.UserRepository;
import com.world_cup.demo.service.TestService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    private final TestService testService;
    private final RedisTemplate<String,Object> redisTemplate;


    public TestController(TestService testService, RedisTemplate<String,Object> redisTemplate, UserRepository userRepository){
        this.testService = testService;
        this.redisTemplate = redisTemplate;
    }

//    @Scheduled(fixedRate = 5000) // Run every 5 seconds
//    public void scheduledTask() {
//        System.out.println("Scheduled task executed at: " + System.currentTimeMillis());
//    }

    @PostMapping("/add")
    public Person addPerson(@RequestBody Person person){
        try{
            Boolean setTest1 = redisTemplate.opsForSet().isMember("set_test", "123");
            Long setTest = redisTemplate.opsForSet().add("set_test", "123");
            String s="";
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return testService.addName(person);
    }

    // Helper endpoint to inspect how the JSON is parsed into a generic Map
    @PostMapping("/addMap")
    public Object addPersonRaw(@RequestBody Map<String, Object> body) {
        System.out.println("Received raw body as Map: " + body);
        return body;
    }

    @PostMapping("/addUser")
    public User addUser(@RequestBody User user){
        return testService.addUser(user);
    }

    @PostMapping("addBet")
    public Bet addBet(@RequestBody BetRequest betRequest){
        return testService.addBet(betRequest);
    }

    @PostMapping("addBetMulti")
    public String addBetMulti(@RequestBody BetRequest betRequest){
        testService.addBetMulti(betRequest);
        return "added bets...";
    }
}
