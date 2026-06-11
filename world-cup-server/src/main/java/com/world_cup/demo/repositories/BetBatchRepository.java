package com.world_cup.demo.repositories;

import com.world_cup.demo.dto.BetToUpdate;
import com.world_cup.demo.dto.BetUpdateItem;
import com.world_cup.demo.entities.Bet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BetBatchRepository{
    private static final Logger logger = LoggerFactory.getLogger(BetBatchRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public BetBatchRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchUpdate(List<Bet> betList,String gameResult,Integer gameId){
        try{
            logger.info("batch update "+ Thread.currentThread().getId());
            String sql = "update bet set result=?,status=? where id=?";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Bet currentBet = betList.get(i);
                    ps.setString(1,gameResult);
                    String status = currentBet.getPrediction().equals(gameResult)?"CORRECT":"WRONG";
                    ps.setString(2,status);
                    ps.setLong(3,currentBet.getId());
                }

                @Override
                public int getBatchSize() {
                    return betList.size();
                }
            });

        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public void batchUpdate(List<BetUpdateItem> betList, String gameResult){
        try{
            logger.info("batch update "+ Thread.currentThread().getId());
            String sql = "update bet set result=?,status= ? where id=?";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    BetUpdateItem currentBet = betList.get(i);
                    ps.setString(1,gameResult);
                    String status = currentBet.getPrediction().equals(gameResult)?"CORRECT":"WRONG";
                    ps.setString(2,status);
                    ps.setLong(3,currentBet.getId());
                }

                @Override
                public int getBatchSize() {
                    return betList.size();
                }
            });

        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
}
