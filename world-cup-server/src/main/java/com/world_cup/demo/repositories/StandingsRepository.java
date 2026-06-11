package com.world_cup.demo.repositories;

import com.world_cup.demo.entities.GroupStandings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandingsRepository extends JpaRepository<GroupStandings,Integer> {

    Optional<GroupStandings> findByGroupNameAndTeamId(String groupName, Integer teamId);

    @Query(value="select * from group_standings order by group_name ASC, points DESC",nativeQuery = true)
    List<GroupStandings> findAllSortByGroupNameAndPoints();

    List<GroupStandings> findByGroupNameOrderByPoints(String groupName);
}
