package com.example.smsweb.utils.ExcelExport;

import com.example.smsweb.dto.teacher.InputMarkModel;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ImportMarkExport {
    private List<InputMarkModel> inputMarkModelList;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    Row row;
    CellStyle style;
    XSSFFont font;

    public ImportMarkExport(List<InputMarkModel> inputMarkModelList) {
        this.inputMarkModelList = inputMarkModelList;
        workbook = new XSSFWorkbook();
    }
    private void writeHeader(){
        sheet = workbook.createSheet("FeedBack");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        createCell(row, 0,"Student Code",style);
        createCell(row, 1, "Subject Code", style);
        createCell(row, 2, "Student Name", style);
        createCell(row, 3, "Subject Name", style);
        createCell(row, 4, "ASM mark", style);
        createCell(row, 5, "OBJ mark", style);
    }

    private void createCell(Row row, int columnCount, Object valueOfCell,CellStyle cellStyle){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);

        if(valueOfCell instanceof  Integer){
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        }else if (valueOfCell instanceof Date) {
            cell.setCellValue((Date) valueOfCell);
        }else if (valueOfCell instanceof Double) {
            cell.setCellValue((Double) valueOfCell);
        }else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(cellStyle);
    }

    private void writeData(){
        style = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        int rowCount = 1;
        for (InputMarkModel item:inputMarkModelList) {
            row = sheet.createRow(rowCount++);
            createCell(row, 0, item.getStudentCode(), style);
            createCell(row, 1, item.getSubjectCode(), style);
            createCell(row, 2, item.getFullName(), style);
            createCell(row, 3, item.getSubjectName(), style);
            createCell(row, 4, item.getAsmMark() == null ?"null":item.getAsmMark(), style);
            createCell(row, 5, item.getObjMark() == null ?"null":item.getObjMark(), style);
        }
    }

    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        writeData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
