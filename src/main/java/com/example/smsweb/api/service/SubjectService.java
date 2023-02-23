package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.ISubject;
import com.example.smsweb.api.di.repository.SubjectResponsitory;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Subject;
import com.example.smsweb.utils.ExcelHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectService implements ISubject {
    @Autowired
    SubjectResponsitory dao;
    List<Subject> listSubject;
    XSSFWorkbook workbook;
    XSSFSheet sheet;

    @Override
    public void save(Subject subject) {
        try {
            dao.save(subject);
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
    public List<Subject> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public Subject findOne(int id) {
        return dao.findById(id).orElseThrow(() -> new ErrorHandler("Không tìm thấy dữ liệu"));
    }

    @Override
    public List<Subject> findSubjectByMajorId(int majorId) {
        try {
            return dao.findAllByMajorId(majorId);
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public boolean importExcelData(MultipartFile file) {
        listSubject = new ArrayList<>();
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheetAt(0);
            for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);
                if (rowIndex == 0) {
                    continue;
                }
                String subject_code = ExcelHelper.getValue(row.getCell(0)).toString();
                String subject_name = ExcelHelper.getValue(row.getCell(1)).toString();
                String subject_fee = ExcelHelper.getValue(row.getCell(2)).toString();
                String subject_slot = ExcelHelper.getValue(row.getCell(3)).toString();
                String subject_semester = ExcelHelper.getValue(row.getCell(4)).toString();
                String subject_major = ExcelHelper.getValue(row.getCell(5)).toString();
                if (!subject_code.isEmpty() && !subject_name.isEmpty()
                        && !subject_fee.isEmpty() && !subject_slot.isEmpty()
                        && !subject_semester.isEmpty() && !subject_major.isEmpty()) {

                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
