package com.mrip1x.exchangeratesmonitor.service.impl;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;
import com.mrip1x.exchangeratesmonitor.service.ExcelBuildService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ExcelBuildServiceImpl implements ExcelBuildService {

    @Override
    public Path buildRatesExcel(List<ExchangeRate> rates) throws IOException {
        Path excelFile = Files.createTempFile("excel-rates", ".xls");

        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Birthdays");

        Row row = sheet.createRow(0);

        row.createCell(0).setCellValue("Date");
        row.createCell(1).setCellValue("Exchange Rate");

        int rowNumber = 1;
        for (ExchangeRate rate : rates) {
            row = sheet.createRow(rowNumber);
            row.createCell(0).setCellValue(rate.getDate().toString());
            row.createCell(1).setCellValue(rate.getRate());
            rowNumber++;
        }

        sheet.autoSizeColumn(1);

        book.write(new FileOutputStream(excelFile.toFile()));
        book.close();

        return excelFile;
    }
}
