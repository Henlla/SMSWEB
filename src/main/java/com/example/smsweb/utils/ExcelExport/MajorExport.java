package com.example.smsweb.utils.ExcelExport;

import com.example.smsweb.models.Major;
import com.example.smsweb.utils.ExcelHelper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class MajorExport {
    XSSFWorkbook workbook;
    XSSFSheet sheet;

    public void exportToExcel(HttpServletResponse response,List<Major> list, String fileName) throws IOException {
        File file = ResourceUtils.getFile("classpath:static/TempExcel/major.xlsx");
        ExcelHelper.setResponseHeader(response, "application/octet-stream", ".xlsx", fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        workbook = new XSSFWorkbook(fileInputStream);
        writeDataLine(list);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void writeDataLine(List<Major> list) throws IOException {
        sheet = workbook.getSheetAt(0);
        int rowIndex = 1;
        for (Major major : list) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnCount = 0;
            ExcelHelper.createCell(row, columnCount++, rowIndex - 1,sheet);
            ExcelHelper.createCell(row, columnCount++, major.getMajorCode(),sheet);
            ExcelHelper.createCell(row, columnCount++, major.getMajorName(),sheet);
        }
    }
}
