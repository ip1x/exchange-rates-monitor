package com.mrip1x.exchangeratesmonitor.controller;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;
import com.mrip1x.exchangeratesmonitor.nbp.DataBaseUpdater;
import com.mrip1x.exchangeratesmonitor.nbp.util.LocalDateStringConverter;
import com.mrip1x.exchangeratesmonitor.service.ExcelBuildService;
import com.mrip1x.exchangeratesmonitor.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Controller
public class ExchangeRateController {
    private final Integer DEFAULT_MAX_CHART_AXIS_X_LENGTH = 30;
    private final String MESSAGE_INCORRECT_FILTERS_DATA = "Fill filters with correct data";

    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private ExcelBuildService excelBuildService;
    @Autowired
    private DataBaseUpdater dataBaseUpdater;
    @Autowired
    private LocalDateStringConverter dateConverter;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView loadIndexPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("msg", "Hello User");
        return modelAndView;
    }

    @RequestMapping(value = "/rates/chart", method = RequestMethod.GET)
    @ResponseBody
    public List<ExchangeRate> getChartRates() {
        return exchangeRateService.getPreparedRatesForChart(DEFAULT_MAX_CHART_AXIS_X_LENGTH);
    }

    @RequestMapping(value = "/rates/table", method = RequestMethod.GET)
    @ResponseBody
    public List<ExchangeRate> getTableRates() {
        return exchangeRateService.getPreparedRatesForTable();
    }


    @RequestMapping(value = "/rates/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity updateRatesFromAPI(@RequestParam String startDate, @RequestParam String endDate) {
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.OK);
        if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
            dataBaseUpdater.updateDataBase();
            dataBaseUpdater.updateTodayRate();
        } else {
            if (validateDates(startDate, endDate, false)) {
                dataBaseUpdater.updateDataBase(dateConverter.convert(startDate), dateConverter.convert(endDate));
            } else {
                responseEntity = new ResponseEntity<>(MESSAGE_INCORRECT_FILTERS_DATA, HttpStatus.BAD_REQUEST);
            }
        }
        return responseEntity;
    }

    @RequestMapping(value = "/rates/filter/table", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity filterDateForTable(@RequestParam String startDate, @RequestParam String endDate) {
        if (validateDates(startDate, endDate, false)) {
            return new ResponseEntity<>(
                    exchangeRateService.getFilteredByDateTableRates(dateConverter.convert(startDate), dateConverter.convert(endDate)),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(MESSAGE_INCORRECT_FILTERS_DATA, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/rates/filter/chart", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity filterDateForChart(@RequestParam String startDate, @RequestParam String endDate) {
        if (validateDates(startDate, endDate, false)) {
            return new ResponseEntity<>(
                    exchangeRateService.getFilteredByDateChartRates(dateConverter.convert(startDate), dateConverter.convert(endDate), DEFAULT_MAX_CHART_AXIS_X_LENGTH),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(MESSAGE_INCORRECT_FILTERS_DATA, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/rates/excel", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity getRatesInExcel(@RequestBody List<ExchangeRate> exchangeRates, HttpServletResponse response) {
        try {
            Path excelFile = excelBuildService.buildRatesExcel(exchangeRates);
            FileSystemResource fileSystemResource = new FileSystemResource(excelFile.toFile());
            response.setHeader("Content-Dispositions", "attachment; filename=\"excel.xsl");
            return new ResponseEntity<>(fileSystemResource, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateDates(String startDate, String endDate, boolean canBeEmpty) {
        boolean isEmpty = StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate);
        if (canBeEmpty && isEmpty) return true;
        if (!canBeEmpty && isEmpty) return false;
        try {
            LocalDate startLocalDate = dateConverter.convert(startDate);
            LocalDate endLocalDate = dateConverter.convert(endDate);
            return endLocalDate.isAfter(startLocalDate) || endLocalDate.isEqual(startLocalDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}