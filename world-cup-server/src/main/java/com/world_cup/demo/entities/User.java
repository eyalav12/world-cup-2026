package com.world_cup.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Entity
@Table(name="app_user")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<Bet> bets;
    private String name;
    private String mail;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    @OneToOne(mappedBy = "user")
    private TournamentWinnerBet tournamentWinnerBet;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public TournamentWinnerBet getTournamentWinnerBet() {
        return tournamentWinnerBet;
    }

    public void setTournamentWinnerBet(TournamentWinnerBet tournamentWinnerBet) {
        this.tournamentWinnerBet = tournamentWinnerBet;
    }

    @ManyToMany
    @JoinTable(
            name="user_grpup",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="group_id")
    )
    private List<Group> groups;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    private Integer score;

    public User(Long id, List<Bet> bets, String name, String mail,Integer score) {
        this.id = id;
        this.bets = bets;
        this.name = name;
        this.mail = mail;
        this.score = score;
    }
    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void setBets(List<Bet> bets) {
        this.bets = bets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
