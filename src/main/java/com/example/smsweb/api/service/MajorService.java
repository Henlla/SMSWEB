package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Major;
import com.example.smsweb.utils.ExcelExport.MajorExport;
import com.example.smsweb.utils.ExcelHelper;
import com.example.smsweb.utils.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MajorService implements IMajor {
    @Autowired
    private MajorRepository dao;
    List<Major> listMajor;
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    private String STORE_URL_IMAGE = "/src/main/resources/static/img/student/";

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
        return dao.findById(id).orElseThrow(() -> new ErrorHandler("Find not found !"));
    }

    @Override
    public String importDataToDb(MultipartFile file) {
        if (!file.isEmpty()) {
            if (FileUtils.getExtension(file.getOriginalFilename()).equals("xlsx")) {
                listMajor = new ArrayList<>();
                try {
                    workbook = new XSSFWorkbook(file.getInputStream());
                    sheet = workbook.getSheetAt(0);
                    for (int rowIndex = 1; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                        XSSFRow row = sheet.getRow(rowIndex);
                        String major_code = ExcelHelper.getValue(row.getCell(0)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(0)).toString();
                        String major_name = ExcelHelper.getValue(row.getCell(1)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(1)).toString();
                        if (!major_code.isEmpty() && !major_name.isEmpty()) {
                            Major major = Major.builder().majorCode(major_code).majorName(major_name).build();
                            listMajor.add(major);
//                            FileUtils.writeImageFromExcel(major_code, STORE_URL_IMAGE, workbook, rowIndex - 1);
                        }
                    }
                    if (!listMajor.isEmpty()) {
                        dao.saveAll(listMajor);
                    } else {
                        return "Excel don't have data";
                    }
                    return "";
                } catch (Exception e) {
                    log.error("Import Major: " + e.getMessage());
                    return "Import data fail";
                }
            } else {
                return "Please choose excel file";
            }
        } else {
            return "Please choose file";
        }
    }

    @Override
    public Major findMajorByMajorCode(String majorCode) {
        return dao.findMajorByMajorCode(majorCode);
    }

    @Override
    public Major findMajorByMajorCodeAndApartment(String majorCode, Integer apartmentId) {
        return dao.findMajorByMajorCodeAndApartmentId(majorCode, apartmentId);
    }

    @Override
    public void exportDataToExcel(HttpServletResponse response, List<Major> list, String fileName) {
        try {
            MajorExport export = new MajorExport();
            export.exportToExcel(response, list, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
