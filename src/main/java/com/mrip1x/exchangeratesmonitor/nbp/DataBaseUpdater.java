package com.mrip1x.exchangeratesmonitor.nbp;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;
import com.mrip1x.exchangeratesmonitor.nbp.dto.NbpResponse;
import com.mrip1x.exchangeratesmonitor.nbp.service.NbpService;
import com.mrip1x.exchangeratesmonitor.nbp.util.ToExchangeRateConverter;
import com.mrip1x.exchangeratesmonitor.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class DataBaseUpdater {
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private ToExchangeRateConverter exchangeRateConverter;
    @Autowired
    private NbpService nbpService;

    @PostConstruct
    private void postConstruct() {
        updateDataBase();
    }

    /**
     * Update today rate each 15 min
     */
    @Scheduled(cron = "0 15 * * * *")
    public void updateTodayRate() {
        log.info("updateTodayRate: updating today rate from NBP API");
        List<ExchangeRate> savedRates = exchangeRateService.saveAll(exchangeRateConverter.convert(nbpService.getTodayRate()));
        log.info("updateTodayRate: number of updated rows = " + savedRates.size());
    }

    @Transactional
    public void updateDataBase() {
        log.info("updateDataBase: updating data base with data from NBP API");
        int updatedRows = 0;
        for (NbpResponse response : nbpService.getRatesTillToday()) {
            List<ExchangeRate> rates = exchangeRateService.saveAll(exchangeRateConverter.convert(response));
            updatedRows += rates.size();
        }
        log.info("updateDataBase: number of updated rows = " + updatedRows);
    }

    @Transactional
    public void updateDataBase(LocalDate startDate, LocalDate endDate) {
        log.info("updateDataBase: updating data base with data from NBP API for period {{}:{}}", startDate, endDate);
        int updatedRows = 0;
        for (NbpResponse response : nbpService.getRatesForPeriod(startDate, endDate)) {
            List<ExchangeRate> rates = exchangeRateService.saveAll(exchangeRateConverter.convert(response));
            updatedRows += rates.size();
        }
        log.info("updateDataBase: number of updated rows = " + updatedRows);
    }
}
