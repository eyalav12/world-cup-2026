package com.world_cup.demo.repositories;

import com.world_cup.demo.entities.HistoryMatchData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryMatchDataRepository extends JpaRepository<HistoryMatchData,Long> {
    @Query(value = "select * from history_match_data where history_match_data.home_team_name=?1 or history_match_data.away_team_name=?1 order by history_match_data.match_date desc limit 10",nativeQuery = true)
    List<HistoryMatchData> getTeamLastMatches(String teamName);

    @Query(value="select * from history_match_data where (home_team_name=?1 and away_team_name=?2) or (home_team_name=?2 and away_team_name=?1) order by match_date desc limit 10",nativeQuery = true)
    List<HistoryMatchData> getHeadToHead(String teamA,String teamB);
}
