package com.world_cup.demo.repositories;

import com.world_cup.demo.entities.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet,Long> {
    @Query(value = "select * from bet where game_id=?1",nativeQuery = true)
    List<Bet> findAllBetsByGameId(Integer gameId);
}


//select * from public.app_user LEFT JOIN (
//        select user_id,sum(score_bet) as total from (
//select bet.id,user_id as user_id,CASE bet.status
//WHEN 'WRONG' THEN 0
//WHEN 'CORRECT' THEN 1
//ELSE 0
//END AS score_bet
//
//from bet) group by user_id)
//on id=user_id

