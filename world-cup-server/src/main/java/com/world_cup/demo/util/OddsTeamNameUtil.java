package com.world_cup.demo.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Map The Odds API team labels to Football Data / DB team names. */
public final class OddsTeamNameUtil {

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("usa", "United States"),
            Map.entry("cote d'ivoire", "Ivory Coast"),
            Map.entry("côte d'ivoire", "Ivory Coast"),
            Map.entry("curacao", "Curaçao"),
            Map.entry("korea republic", "South Korea"),
            Map.entry("republic of korea", "South Korea")
    );

    private OddsTeamNameUtil() {}

    public static String normalize(String oddsApiName, Collection<String> dbTeamNames) {
        if (oddsApiName == null || oddsApiName.isBlank()) {
            return oddsApiName;
        }
        String trimmed = oddsApiName.trim();
        Set<String> teams = Set.copyOf(dbTeamNames);

        if (teams.contains(trimmed)) {
            return trimmed;
        }

        String alias = ALIASES.get(trimmed.toLowerCase(Locale.ROOT));
        if (alias != null && teams.contains(alias)) {
            return alias;
        }

        for (String dbName : teams) {
            if (dbName.equalsIgnoreCase(trimmed)) {
                return dbName;
            }
        }

        return trimmed;
    }
}
