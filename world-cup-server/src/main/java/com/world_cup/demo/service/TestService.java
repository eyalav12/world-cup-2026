package com.world_cup.demo.service;

import com.world_cup.demo.dto.BetRequest;
import com.world_cup.demo.entities.Bet;
import com.world_cup.demo.entities.Person;
import com.world_cup.demo.entities.User;
import com.world_cup.demo.exception.UserNotFoundException;
import com.world_cup.demo.repositories.BetRepository;
import com.world_cup.demo.repositories.PersonRepository;
import com.world_cup.demo.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TestService {
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final BetRepository betRepository;

    private final static Logger logger = LoggerFactory.getLogger(TestService.class);

    public TestService(PersonRepository personRepository,UserRepository userRepository,BetRepository betRepository){
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.betRepository = betRepository;
    }

    public User addUser(User user){
        List<String> names = List.of("Rafa","Roger","Novak","Carlos","Juan","Yannik","Eyal","Sergio","Leo","Cris");
        try{
//            for(int i=0;i<6000;i++){
//                User newUser = new User();
//                newUser.setMail("gmail@gmail.com");
//                Random random = new Random();
//                int i1 = random.nextInt(0, names.size());
//                newUser.setName(names.get(i1));
//                User saved=userRepository.save(newUser);

//            }
            User saved = userRepository.save(user);
            return saved;
//            User saved = userRepository.save(user);
//            return user;

        }
        catch(Exception e){
            logger.info("error in save",e.getMessage());
            return null;
        }
    }
    public void addBetMulti(BetRequest betRequest){
        List<String> results = List.of("HOME_TEAM","AWAY_TEAM","DRAW");
        for(int i=0;i<6000;i++){
            try{
                Random random = new Random();
                int i1 = random.nextInt(0, results.size());
                int finalI = i;
                User user = userRepository.findById(betRequest.getUserId()+i).orElseThrow(()-> new UserNotFoundException("user"+ finalI +" NotFound"));
                Bet bet = new Bet();
                bet.setHomeTeam(betRequest.getHomeTeam());
                bet.setAwayTeam(betRequest.getAwayTeam());
                bet.setPrediction(results.get(i1));
                bet.setGameId(betRequest.getGameId());
                bet.setDate(betRequest.getDate());
                bet.setStage(betRequest.getStage());
                bet.setUser(user);
                Bet saved= betRepository.save(bet);
            }
            catch(Exception e){
                logger.info(e.getMessage());
            }

        }

    }
    public Bet addBet(BetRequest betRequest){
        try{
            User user = userRepository.findById(betRequest.getUserId()).orElseThrow(()-> new UserNotFoundException("user"+ betRequest.getUserId() +" NotFound"));
            Bet bet = new Bet();
            bet.setHomeTeam(betRequest.getHomeTeam());
            bet.setAwayTeam(betRequest.getAwayTeam());
            bet.setPrediction(betRequest.getPrediction());
            bet.setGameId(betRequest.getGameId());
            bet.setUser(user);
            Bet saved= betRepository.save(bet);
            return saved;
        }
        catch(Exception e){
            logger.info(e.getMessage());
        }
        return null;



    }
    public Person addName(Person person){
        try{
            Person save = personRepository.save(person);
            return save;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}
