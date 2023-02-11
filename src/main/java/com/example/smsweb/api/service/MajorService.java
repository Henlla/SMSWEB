package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Major;
import com.example.smsweb.utils.ExcelExport.MajorExport;
import com.example.smsweb.utils.ExcelHelper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public List<Major> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public Major findOne(int id) {
        try {
            return dao.findById(id).get();
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public void importDataToDb(MultipartFile file) {
        if (!file.isEmpty()) {
            listMajor = new ArrayList<>();
            try {
                workbook = new XSSFWorkbook(file.getInputStream());
                sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    if (rowIndex == 0) {
                        continue;
                    }
                    String major_code = ExcelHelper.getValue(row.getCell(0)).toString();
                    String major_name = ExcelHelper.getValue(row.getCell(0)).toString();
                    if (!major_code.isEmpty() && !major_name.isEmpty()) {
                        Major major = Major.builder().majorCode(major_code).majorName(major_name).build();
                        listMajor.add(major);
                    }
                }
            } catch (Exception e) {
                throw new ErrorHandler(e.getMessage());
            }
            if (!listMajor.isEmpty()) {
                dao.saveAll(listMajor);
            } else {
                throw new ErrorHandler("Không có dữ liệu");
            }
        }
    }

    @Override
    public void exportDataToExcel(HttpServletResponse response, List<Major> list, String fileName) {
        try {
            MajorExport export = new MajorExport();
            export.exportToExcel(response,list, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
