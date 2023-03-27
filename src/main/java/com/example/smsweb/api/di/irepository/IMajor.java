package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Major;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMajor extends IGenericRepository<Major> {
    String importDataToDb(MultipartFile file);

    Major findMajorByMajorCode(String majorCode);

    void exportDataToExcel(HttpServletResponse response, List<Major> list, String fileName);
}
