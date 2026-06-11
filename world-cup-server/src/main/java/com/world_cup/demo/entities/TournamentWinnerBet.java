package com.world_cup.demo.entities;

import jakarta.persistence.*;

@Entity
public class TournamentWinnerBet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String winnerTeamName;

    private Integer winnerTeamId;
}
