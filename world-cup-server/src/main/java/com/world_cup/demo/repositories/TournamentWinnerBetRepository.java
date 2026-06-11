package com.world_cup.demo.repositories;

import com.world_cup.demo.entities.TournamentWinnerBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentWinnerBetRepository extends JpaRepository<TournamentWinnerBet,Integer> {
}
