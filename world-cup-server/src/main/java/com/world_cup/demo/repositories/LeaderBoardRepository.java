package com.world_cup.demo.repositories;

import com.world_cup.demo.dto.UserLeaderBoardDto;
import com.world_cup.demo.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LeaderBoardRepository {

    private static final Logger logger = LoggerFactory.getLogger(LeaderBoardRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public LeaderBoardRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserLeaderBoardDto> getGlobalLeaderBoardPage(int pageSize, int pageNumber){
        int offset = pageNumber * pageSize;
        String sql = "select * from app_user left join(\n" +
                "\n" +
                "    select user_id,SUM(\n" +
                "               CASE\n" +
                "                    WHEN status = 'CORRECT' THEN 1\n" +
                "                    ELSE 0\n" +
                "               END\n" +
                "               ) as total\n" +
                "    from bet group by user_id\n" +
                ")t on app_user.id = user_id order by total desc,name asc limit ? offset ?";
        return jdbcTemplate.query(
                sql,
                new RowMapper<UserLeaderBoardDto>() {
                    @Override
                    public UserLeaderBoardDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new UserLeaderBoardDto(
                                rs.getString("name"),
                                rs.getInt("total")
                        );
                    }
                },pageSize, offset
        );
    }

}
