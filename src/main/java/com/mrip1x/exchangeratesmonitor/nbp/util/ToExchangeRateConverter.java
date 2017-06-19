package com.mrip1x.exchangeratesmonitor.nbp.util;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;
import com.mrip1x.exchangeratesmonitor.nbp.dto.NbpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ToExchangeRateConverter implements Converter<NbpResponse, List<ExchangeRate>> {
    @Autowired
    private LocalDateStringConverter dateConverter;

    @Override
    public List<ExchangeRate> convert(NbpResponse exchangeRatesResponse) {
        List<ExchangeRate> exchangeRates = new ArrayList<>(exchangeRatesResponse.getRates().length);
        for (NbpResponse.Rate rate : exchangeRatesResponse.getRates()) {
            exchangeRates.add(new ExchangeRate(dateConverter.convert(rate.getEffectiveDate()),
                    Double.valueOf(rate.getMid())));
        }
        return exchangeRates;
    }
}
