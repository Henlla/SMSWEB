package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Major;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MajorService implements IMajor {
    @Autowired
    private MajorRepository dao;
    List<Major> listMajor;
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    @Override
    public void save(Major major) {
        try {
            dao.save(major);
        } catch (Exception e) {
            throw new ErrorHandler("Lưu thành công");
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler("Xóa thất bại");
        }
    }

    @Override
    public List<Major> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy dữ liệu");
        }
    }

    @Override
    public Major findOne(int id) {
        try {
            return dao.findById(id).get();
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy major với id : " + id);
        }
    }

    @Override
    public void importDataToDb(MultipartFile file) {
        if (!file.isEmpty()) {
            listMajor = new ArrayList<>();
            try {
                workbook = new XSSFWorkbook(file.getInputStream());
                sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    if (rowIndex == 0) {
                        continue;
                    }
                    String major_code = getValue(row.getCell(0)).toString();
                    String major_name = getValue(row.getCell(0)).toString();
                    if (!major_code.isEmpty() && !major_name.isEmpty()) {
                        Major major = Major.builder().majorCode(major_code).majorName(major_name).build();
                        listMajor.add(major);
                    }
                }
            } catch (Exception e) {
                throw new ErrorHandler("Đỗ dữ liệu thất bại");
            }
            if (!listMajor.isEmpty()) {
                dao.saveAll(listMajor);
            } else {
                throw new ErrorHandler("Không có dữ liệu");
            }
        }
    }

    @Override
    public void exportDataToExcel(HttpServletResponse response) {
        try {
            writeHeaders();
            write();
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeHeaders(){
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Major");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(13);
        style.setFont(font);
        createCell(row,0,"STT",style);
        createCell(row,1,"Major Code",style);
        createCell(row,2,"Major Name",style);
    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(style);
    }

    private void write() {
        listMajor = new ArrayList<>();
        listMajor = findAll();
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        for (Major record: listMajor) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++,rowCount - 1, style);
            createCell(row, columnCount++, record.getMajorCode(), style);
            createCell(row, columnCount++, record.getMajorName(), style);
        }
    }

    private Object getValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            case _NONE:
                return null;
            default:
                break;
        }
        return null;
    }

    public static int getNumberOfNonEmptyCells(XSSFSheet sheet, int columnIndex) {
        int numOfNonEmptyCells = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                XSSFCell cell = row.getCell(columnIndex);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    numOfNonEmptyCells++;
                }
            }
        }
        return numOfNonEmptyCells;
    }
}
