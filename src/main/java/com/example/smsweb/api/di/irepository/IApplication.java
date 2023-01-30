package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Application;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IApplication extends IGenericRepository<Application> {
    public void importDataToDb(List<MultipartFile> file);
}
