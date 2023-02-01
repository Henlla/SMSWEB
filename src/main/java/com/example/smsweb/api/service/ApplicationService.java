package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IApplication;
import com.example.smsweb.api.di.repository.ApplicationRepository;
import com.example.smsweb.models.Application;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService implements IApplication {
    @Autowired
    public ApplicationRepository dao;
    List<Application> listApplication;
    @Override
    public void save(Application application) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Application> findAll() {
        return null;
    }

    @Override
    public Application findOne(int id) {
        return null;
    }

    @Override
    public void importDataToDb(List<MultipartFile> file) {
        if(file.isEmpty()){
            listApplication = new ArrayList<>();
            file.forEach(files ->{
                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    for (int rowIndex = 0; rowIndex < getNumberOfNonEmptyCells(sheet,0)- 1;rowIndex++){
                        XSSFRow row = sheet.getRow(rowIndex);
                        if(rowIndex==0){
                            continue;
                        }
                    }
                }catch (Exception e){
                    e.getMessage();
                }
            });
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
