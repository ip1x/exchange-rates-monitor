package com.mrip1x.exchangeratesmonitor.service;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;


public interface ExchangeRateService {
    Sort SORT_RATES_BY_DATE_ASC = new Sort(Sort.Direction.ASC, "date");
    Sort SORT_RATES_BY_DATE_DESC = new Sort(Sort.Direction.DESC, "date");

    ExchangeRate save(ExchangeRate exchangeRate);

    List<ExchangeRate> saveAll(List<ExchangeRate> exchangeRates);

    List<ExchangeRate> findAll(Sort sort);

    List<ExchangeRate> getFilteredByDateChartRates(LocalDate startDate, LocalDate endDate, Integer maxSizeOfMergedRates);

    List<ExchangeRate> getFilteredByDateTableRates(LocalDate startDate, LocalDate endDate);

    List<ExchangeRate> getPreparedRatesForChart(Integer maxSizeOfMergedRates);

    List<ExchangeRate> getPreparedRatesForTable();
}
