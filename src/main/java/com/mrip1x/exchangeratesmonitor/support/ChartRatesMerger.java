package com.mrip1x.exchangeratesmonitor.support;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChartRatesMerger {
    public static List<ExchangeRate> mergeRates(List<ExchangeRate> rates, int maxSizeOfMergedRates) {
        int k = (int) ((double) rates.size() / maxSizeOfMergedRates + 0.5);
        k = k == 0 ? 1 : k;
        List<ExchangeRate> mergedRates = new ArrayList<>(rates.size() / k);
        double sum = 0;
        int i = 0;
        Iterator<ExchangeRate> iterator = rates.iterator();
        while (iterator.hasNext()) {
            ExchangeRate rate = iterator.next();
            sum += rate.getRate();
            i++;
            if (i % k == 0 || !iterator.hasNext()) {
                mergedRates.add(new ExchangeRate(rate.getDate(), sum / i));
                sum = 0;
                i = 0;
            }
        }
        return mergedRates;
    }
}
