package com.mrip1x.exchangeratesmonitor.service.impl;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;
import com.mrip1x.exchangeratesmonitor.repository.ExchangeRateRepository;
import com.mrip1x.exchangeratesmonitor.service.ExchangeRateService;
import com.mrip1x.exchangeratesmonitor.support.ChartRatesMerger;
import com.mrip1x.exchangeratesmonitor.support.TableRatesBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Autowired
    private ExchangeRateRepository repository;

    public ExchangeRate save(ExchangeRate exchangeRate) {
        return repository.save(exchangeRate);
    }

    @Override
    public List<ExchangeRate> saveAll(List<ExchangeRate> exchangeRates) {
        return repository.save(exchangeRates);
    }

    public List<ExchangeRate> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public List<ExchangeRate> getFilteredByDateChartRates(LocalDate startDate, LocalDate endDate, Integer maxSizeOfMergedRates) {
        List<ExchangeRate> rates = repository.findByDateBetween(startDate, endDate, ExchangeRateService.SORT_RATES_BY_DATE_ASC);
        return ChartRatesMerger.mergeRates(rates, maxSizeOfMergedRates);
    }

    @Override
    public List<ExchangeRate> getFilteredByDateTableRates(@NotEmpty LocalDate startDate, @NotEmpty LocalDate endDate) {
        List<ExchangeRate> rates = repository.findByDateBetween(startDate, endDate, ExchangeRateService.SORT_RATES_BY_DATE_DESC);
        return TableRatesBuilder.fillGaps(rates, true);
    }

    @Override
    public List<ExchangeRate> getPreparedRatesForChart(Integer maxSizeOfMergedRates) {
        List<ExchangeRate> rates = repository.findAll(ExchangeRateService.SORT_RATES_BY_DATE_ASC);
        return ChartRatesMerger.mergeRates(rates, maxSizeOfMergedRates);
    }

    @Override
    public List<ExchangeRate> getPreparedRatesForTable() {
        List<ExchangeRate> rates = repository.findAll(ExchangeRateService.SORT_RATES_BY_DATE_DESC);
        return TableRatesBuilder.fillGaps(rates, true);
    }
}
