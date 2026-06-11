package com.world_cup.demo.util.apiUtils;


import com.world_cup.demo.client.FootballDataClient;
import com.world_cup.demo.client.OddsApiClient;
import com.world_cup.demo.dto.MatchDto;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OddsApiUtil {

    private final OddsApiClient oddsApiClient;
    private final ObjectMapper objectMapper;

    public OddsApiUtil(OddsApiClient oddsApiClient, ObjectMapper objectMapper){
        this.oddsApiClient = oddsApiClient;
        this.objectMapper = objectMapper;
    }

    public String httpCallToApi(){
        return oddsApiClient.fetchMatchesOdds();
    }

    public String convertApiResponseToOddsDto(String apiResponse){
        return apiResponse;
    }

//    public String httpCallToApi(Integer teamId){
//        LocalDate today = LocalDate.now();
//        LocalDate twoYearsAgoDate = today.minusYears(2);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String now = today.format(formatter);
//        String twoYearsAgo = twoYearsAgoDate.format(formatter);
//        return footballDataClient.fetchTeamMatches(teamId, twoYearsAgo, now);
//    }

//    @SuppressWarnings({"rawtypes","unchecked"})
//    public List<MatchDto> convertApiResponseToDtoMatchResult(String body){
//        List<MatchDto> matchDtoList = new ArrayList<>();
//        if(body == null || body.isEmpty()) return matchDtoList;
//        try{
//            Map map = objectMapper.readValue(body, Map.class);
//            List<Map> matches = (List<Map>)map.get("matches");
//            if(matches == null) return matchDtoList;
//            for(Map m:matches){
//                Number idNum = (Number)m.get("id");
//                Integer id = idNum == null ? null : idNum.intValue();
//
//                Map homeTeamMap = (Map)m.get("homeTeam");
//                Map awayTeamMap = (Map)m.get("awayTeam");
//                String homeTeam = homeTeamMap == null ? null : (String)homeTeamMap.get("name");
//                String awayTeam = awayTeamMap == null ? null : (String)awayTeamMap.get("name");
//
//                Map scoreMap = (Map)m.get("score");
//                String result = null;
//                Map fullTimeRes = null;
//                if(scoreMap != null){
//                    Object winnerObj = scoreMap.get("winner");
//                    result = winnerObj == null ? null : String.valueOf(winnerObj);
//                    fullTimeRes = (Map)scoreMap.get("fullTime");
//                }
//
//                String score = "";
//                if(fullTimeRes != null){
//                    Object h = fullTimeRes.get("home");
//                    Object a = fullTimeRes.get("away");
//                    String hs = h == null ? "0" : String.valueOf(((Number)h).intValue());
//                    String as = a == null ? "0" : String.valueOf(((Number)a).intValue());
//                    score = hs + "-" + as;
//                }
//
//                String stage = (String) m.get("stage");
//                String matchDate = (String)m.get("utcDate");
//                Map competitionMap = (Map) m.get("competition");
//                String competition = competitionMap == null ? null : (String)competitionMap.get("name");
//                String status = (String)m.get("status");
//
//                MatchDto matchDto = new MatchDto(result,score,awayTeam,matchDate,id,stage,competition, status,homeTeam);
//                matchDtoList.add(matchDto);
//            }
//            return matchDtoList;
//        }
//        catch(Exception e){
//            // keep simple handling; callers can handle empty lists
//            return matchDtoList;
//        }
//    }
}
