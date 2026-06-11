package com.world_cup.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String teamName;
    private Integer teamId;
    private String groupId;

    public Team(){

    }

    public Team(String teamName, Integer teamId,String group) {
        this.teamName = teamName;
        this.teamId = teamId;
        this.groupId = group;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String group) {
        this.groupId = group;
    }
}
