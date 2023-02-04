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
}
