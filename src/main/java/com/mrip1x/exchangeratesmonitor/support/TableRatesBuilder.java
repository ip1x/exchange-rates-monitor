package com.mrip1x.exchangeratesmonitor.support;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;

import java.time.LocalDate;
import java.util.List;

public class TableRatesBuilder {
    public static List<ExchangeRate> fillGaps(List<ExchangeRate> rates, boolean descending) {
        int diff = descending ? -1 : 1;
        for (int i = 0; i < rates.size() - 1; i++) {
            ExchangeRate rate = rates.get(i);
            ExchangeRate nextRate = rates.get(i + 1);
            LocalDate nextDate = rate.getDate().plusDays(diff);
            if (!nextDate.equals(nextRate.getDate())) {
                rates.add(i + 1, new ExchangeRate(nextDate, rate.getRate()));
            }
        }
        return rates;
    }
}
