package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Major;
import com.example.smsweb.models.Mark;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMark extends IGenericRepository<Mark> {
    void saveAll(List<Mark> markList);
    Mark findMarkByStudentSubjectId(int id);
}
