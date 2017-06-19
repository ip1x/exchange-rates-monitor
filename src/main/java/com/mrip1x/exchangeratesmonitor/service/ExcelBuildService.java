package com.mrip1x.exchangeratesmonitor.service;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ExcelBuildService {
    Path buildRatesExcel(List<ExchangeRate> rates) throws IOException;
}
