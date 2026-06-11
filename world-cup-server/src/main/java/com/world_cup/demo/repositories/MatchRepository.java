package com.world_cup.demo.repositories;

import com.world_cup.demo.dto.MatchDto;
import com.world_cup.demo.entities.Match;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match,Integer> {
    List<Match> getByMatchDate(LocalDate localDate);
    Match findByMatchId(Integer matchId);

    @Query(value = "select match.odds_api_id,match.id,match.competition, match.match_id, match.away_team, match.home_team, " +
            "match.score, match.result, match.stage, match.status, match.match_date " +
            "from match where CAST(match_date AS date) = CAST(:date AS date)", nativeQuery = true)
    List<Match> findMatchesByDate(@Param("date") String date);

    @Query(value = "select * from match where (match.home_team=?1 or match.away_team=?1) and match.status='TIMED' ", nativeQuery = true)
    List<Match> findUpcomingMatchesByTeamName(String teamName);

    @Query(value = "select match.match_id from match where (home_team=?1 or away_team=?2) and match_date=?3",nativeQuery = true)
    Integer findMatchByCombinationOfTeamsAndDate(String homeTeam,String awayTeam,String date);

    @Query(value = "select * from match where ((home_team = ?1 and away_team = ?2) or (home_team = ?2 and away_team = ?1)) and status = 'TIMED' order by match_date asc limit 1", nativeQuery = true)
    Match findUpcomingMatchByTeamNames(String teamA, String teamB);

    @Query(value = "select * from match where ((home_team = ?1 and away_team = ?2) or (home_team = ?2 and away_team = ?1)) and cast(match_date as date) = cast(?3 as date) order by match_date asc limit 1", nativeQuery = true)
    Match findMatchByTeamNamesAndDate(String teamA, String teamB, String matchDate);

    @Query(value = "select match.* from match left join team on match.away_team=team.team_name or match.home_team=team.team_name where group_id=?1",nativeQuery = true)
    List<Match> findMatchesByGroupName(String groupName);
    Match findMatchByOddsApiId(String oddsApiId);

    List<Match> findByStatusIn(List<String> statuses);

    @Query(value = "select * from match where status = 'FINISHED' order by match_date desc limit :limit", nativeQuery = true)
    List<Match> findRecentFinishedMatches(@Param("limit") int limit);

    @Query(value = "select * from match where (home_team = :teamName or away_team = :teamName) and status = :status order by match_date desc limit :limit", nativeQuery = true)
    List<Match> findMatchesByTeamNameAndStatus(@Param("teamName") String teamName, @Param("status") String status, @Param("limit") int limit);

    @Query(value = "select * from match where (home_team = :teamName or away_team = :teamName) and status = :status order by match_date desc", nativeQuery = true)
    List<Match> findMatchesByTeamNameAndStatusAll(@Param("teamName") String teamName, @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "update match set odds_api_id =?2 where match_id=?1",nativeQuery = true)
    void updateMatchWithOddsApiId(Integer matchId,String oddsId);



}
