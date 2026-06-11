package com.world_cup.demo.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class DateUtil {

    public String localDateObjectToString(LocalDate localDate,DateTimeFormatter formatter){
        return localDate.format(formatter);
    }

    public LocalDate parseInstantStringToLocalDate(String instantStr){
        Instant instant = Instant.parse(instantStr);
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
