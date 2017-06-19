package com.mrip1x.exchangeratesmonitor.nbp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateStringConverter {
    @Value("${exchange-rates-monitor.nbp.date-format}")
    private String dateFormat;

    private DateTimeFormatter dateTimeFormatter;

    @PostConstruct
    private void initFormatter() {
        dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public LocalDate convert(String date) {
        return LocalDate.parse(date, dateTimeFormatter);
    }

    public String convert(LocalDate date) {
        return date.format(dateTimeFormatter);
    }

}
