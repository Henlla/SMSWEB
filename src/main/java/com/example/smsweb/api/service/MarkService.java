package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.di.irepository.IMark;
import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.api.di.repository.MarkRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Major;
import com.example.smsweb.models.Mark;
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
import java.util.Optional;

@Service
@Slf4j
public class MarkService implements IMark {
    @Autowired
    private MarkRepository dao;

    @Override
    public void saveAll(List<Mark> markList) {
        dao.saveAll(markList);
    }

    @Override
    public Mark findMarkByStudentSubjectId(int id) {
        dao.findFirstByStudentSubjectId(id);
        if (dao.findFirstByStudentSubjectId(id).isPresent()){
            return dao.findFirstByStudentSubjectId(id).get();
        }else{
            return null;
        }
    }

    @Override
    public void save(Mark mark) {
        dao.save(mark);
    }

    @Override
    public void delete(int id) {
        dao.deleteById(id);
    }

    @Override
    public List<Mark> findAll() {
        return dao.findAll();
    }

    @Override
    public Mark findOne(int id) {
        return dao.findById(id).orElseThrow(()-> new ErrorHandler("Cannot find any record"));
    }
}
