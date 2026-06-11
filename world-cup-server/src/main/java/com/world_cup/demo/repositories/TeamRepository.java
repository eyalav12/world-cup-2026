package com.world_cup.demo.repositories;

import com.world_cup.demo.entities.Player;
import com.world_cup.demo.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team,Integer> {

    public Team getByTeamName(String teamName);

    @Query(value="select player.id,player.player_id,player.name,player.position,player.team_id from player left join team on player.team_id = team.team_id where team_name=?1",nativeQuery = true)
    List<Player> findAllPlayersByTeamName(String teamName);

    List<Player> findByTeamId(String teamID);

    List<Team> findByGroupId(String groupId);

    @Query(value="select distinct team.group_id from team",nativeQuery = true)
    List<String> findGroupNames();

    Optional<Team> findByTeamName(String teamName);


}
