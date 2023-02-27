package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IClass extends IGenericRepository<Classses> {
    Classses findByClassCode(String classCode);
    List<String> searchClasssesByClassCode(String listStudentCard);

    List<Classses> findClassByTeacherId(Integer id);
}
