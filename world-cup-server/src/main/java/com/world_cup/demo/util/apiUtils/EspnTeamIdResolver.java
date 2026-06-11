package com.world_cup.demo.util.apiUtils;

import com.world_cup.demo.dto.espn.EspnLeague;
import com.world_cup.demo.dto.espn.EspnSport;
import com.world_cup.demo.dto.espn.EspnTeam;
import com.world_cup.demo.dto.espn.EspnTeamWrapper;
import com.world_cup.demo.dto.espn.EspnTeamsApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EspnTeamIdResolver {

    private static final Logger logger = LoggerFactory.getLogger(EspnTeamIdResolver.class);

    private static final Map<String, String> TEAM_NAME_ALIASES = Map.ofEntries(
            Map.entry("USA", "United States"),
            Map.entry("Korea Republic", "South Korea"),
            Map.entry("Côte d'Ivoire", "Ivory Coast"),
            Map.entry("Cote d'Ivoire", "Ivory Coast"),
            Map.entry("Turkey", "Türkiye")
    );

    public Map<String, String> resolveEspnTeamIds(List<String> teamNames, String teamsApiResponse) {
        Map<String, String> displayNameToId = parseDisplayNameToIdMap(teamsApiResponse);
        Map<String, String> resolved = new HashMap<>();

        for (String teamName : teamNames) {
            String espnTeamId = resolveSingleTeamId(teamName, displayNameToId);
            if (espnTeamId != null) {
                resolved.put(teamName, espnTeamId);
            } else {
                logger.warn("No ESPN team id found for {}", teamName);
            }
        }

        return resolved;
    }

    public String resolveSingleTeamId(String teamName, Map<String, String> displayNameToId) {
        String espnDisplayName = TEAM_NAME_ALIASES.getOrDefault(teamName, teamName);

        if (displayNameToId.containsKey(espnDisplayName)) {
            return displayNameToId.get(espnDisplayName);
        }

        for (Map.Entry<String, String> entry : displayNameToId.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(espnDisplayName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private Map<String, String> parseDisplayNameToIdMap(String teamsApiResponse) {
        Map<String, String> displayNameToId = new HashMap<>();

        if (teamsApiResponse == null || teamsApiResponse.isBlank()) {
            return displayNameToId;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EspnTeamsApiResponse response = objectMapper.readValue(teamsApiResponse, EspnTeamsApiResponse.class);

            if (response.sports() == null) {
                return displayNameToId;
            }

            for (EspnSport sport : response.sports()) {
                if (sport.leagues() == null) {
                    continue;
                }
                for (EspnLeague league : sport.leagues()) {
                    if (league.teams() == null) {
                        continue;
                    }
                    for (EspnTeamWrapper wrapper : league.teams()) {
                        EspnTeam team = wrapper.team();
                        if (team != null && team.displayName() != null && team.id() != null) {
                            displayNameToId.put(team.displayName(), team.id());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse ESPN teams response", e);
        }

        return displayNameToId;
    }
}
