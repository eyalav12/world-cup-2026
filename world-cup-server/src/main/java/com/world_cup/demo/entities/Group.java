package com.world_cup.demo.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String groupCode;

    private String groupName;

    @ManyToMany(mappedBy = "groups")
    private List<User> usersList;


}
