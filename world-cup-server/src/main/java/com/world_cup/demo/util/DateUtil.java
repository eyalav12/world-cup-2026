package com.world_cup.demo.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DateUtil {

    public static final ZoneId TOURNAMENT_ZONE = ZoneId.of("America/New_York");
    public String localDateObjectToString(LocalDate localDate,DateTimeFormatter formatter){
        return localDate.format(formatter);
    }

    public LocalDate parseInstantStringToLocalDate(String instantStr){
        Instant instant = Instant.parse(instantStr);
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }

    public LocalDate todayInTournamentZone() {
        return LocalDate.now(TOURNAMENT_ZONE);
    }

    public Instant parseInstant(String instantStr) {
        return Instant.parse(instantStr);
    }

    /** Calendar date in tournament timezone (matches /matches/byDate bucketing). */
    public static String toTournamentDateKey(String instantStr) {
        if (instantStr == null || instantStr.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(instantStr).atZone(TOURNAMENT_ZONE).toLocalDate().toString();
        } catch (DateTimeParseException e) {
            return instantStr.length() >= 10 ? instantStr.substring(0, 10) : instantStr;
        }
    }
}
