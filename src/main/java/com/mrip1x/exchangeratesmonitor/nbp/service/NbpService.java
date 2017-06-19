package com.mrip1x.exchangeratesmonitor.nbp.service;

import com.mrip1x.exchangeratesmonitor.nbp.dto.NbpResponse;
import com.mrip1x.exchangeratesmonitor.nbp.util.LocalDateStringConverter;
import com.mrip1x.exchangeratesmonitor.nbp.util.ToExchangeRateConverter;
import com.mrip1x.exchangeratesmonitor.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NbpService {

    @Value("${exchange-rates-monitor.nbp.query.usd-rates-for-today}")
    private String USD_RATES_FOR_TODAY_API_URL;
    @Value("${exchange-rates-monitor.nbp.query.usd-rates-for-period}")
    private String USD_RATES_FOR_PERIOD_API_URL;
    @Value("${exchange-rates-monitor.nbp.date-format}")
    private String dateFormat;
    @Value("${exchange-rates-monitor.nbp.query.start-date}")
    private String startDateFromConfig;
    @Value("${exchange-rates-monitor.nbp.query.start-date-param}")
    private String startDateParamName;
    @Value("${exchange-rates-monitor.nbp.query.end-date-param}")
    private String endDateParamName;
    @Value("${exchange-rates-monitor.nbp.query.max-days-per-request}")
    private Integer maxDaysPerRequest;

    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private LocalDateStringConverter dateConverter;
    @Autowired
    private ToExchangeRateConverter exchangeRateConverter;

    private RestTemplate restTemplate = new RestTemplate();

    public NbpResponse getTodayRate() {
        log.debug("getTodayRate: retrieving today rates");
        long time = System.currentTimeMillis();
        ResponseEntity<NbpResponse> response = sendRequest(USD_RATES_FOR_TODAY_API_URL);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            log.info("getTodayRate: rates retrieved successfully");
            return response.getBody();
        }
        log.info("getTodayRate: rates retrieving took {} millis", System.currentTimeMillis() - time);
        return null;
    }

    public List<NbpResponse> getRatesTillToday() {
        return getRatesForPeriod(dateConverter.convert(startDateFromConfig), LocalDate.now());
    }

    public List<NbpResponse> getRatesForPeriod(LocalDate startDate, LocalDate endDate) {
        List<String> dates = getPeriods(startDate, endDate);
        List<NbpResponse> responseEntities = new ArrayList<>(dates.size() - 1);
        for (int i = 0; i < dates.size() - 1; i++) {
            log.debug("getRatesTillToday: retrieving rates for period {} : {}", dates.get(i), dates.get(i + 1));
            long time = System.currentTimeMillis();
            try {
                ResponseEntity<NbpResponse> response = sendRequest(buildUrlForGettingRatesTillToday(dates.get(i), dates.get(i + 1)));

                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    responseEntities.add(response.getBody());
                    log.debug("getRatesTillToday: rates retrieved successfully");
                }
            } catch (HttpClientErrorException e) {
                log.info("No response for request.");
            }
            log.info("getRatesForPeriod {{}:{}} took {} millis", dates.get(i), dates.get(i + 1), System.currentTimeMillis() - time);
        }

        log.info("getRatesForPeriod: rates retrieved successfully");

        return responseEntities;
    }

    private List<String> getPeriods(LocalDate startDate, LocalDate endDate) {
        List<String> dates = new ArrayList<>();
        while (endDate.isAfter(startDate)) {
            dates.add(dateConverter.convert(startDate));
            startDate = startDate.plusDays(maxDaysPerRequest);
        }
        dates.add(dateConverter.convert(LocalDate.now().minusDays(1)));
        return dates;
    }

    private ResponseEntity<NbpResponse> sendRequest(String request) {
        return restTemplate.getForEntity(request, NbpResponse.class);
    }

    private String buildUrlForGettingRatesTillToday(String startDate, String endDate) {
        return USD_RATES_FOR_PERIOD_API_URL.replace(startDateParamName, startDate)
                .replace(endDateParamName, endDate);
    }
}
