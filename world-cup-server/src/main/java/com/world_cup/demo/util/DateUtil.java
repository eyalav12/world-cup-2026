package com.world_cup.demo.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class DateUtil {

    public static final ZoneId TOURNAMENT_ZONE = ZoneId.of("America/New_York");

    public String localDateObjectToString(LocalDate localDate, DateTimeFormatter formatter) {
        return localDate.format(formatter);
    }

    public LocalDate parseInstantStringToLocalDate(String instantStr) {
        return parseInstant(instantStr).atZone(TOURNAMENT_ZONE).toLocalDate();
    }

    public Instant parseInstant(String instantStr) {
        return Instant.parse(instantStr);
    }

    public LocalDate todayInTournamentZone() {
        return LocalDate.now(TOURNAMENT_ZONE);
    }

    /** @deprecated prefer parseInstantStringToLocalDate (tournament calendar day). */
    public LocalDate parseInstantStringToLocalDateUtc(String instantStr) {
        Instant instant = Instant.parse(instantStr);
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
