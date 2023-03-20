package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Subject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISubject extends IGenericRepository<Subject> {
    List<Subject> findSubjectByMajorId(int majorId);
    List<Subject> findSubjectByMajorIdSemester(Integer majorId,Integer semester);
    List<Subject> findSubjectBySemesterIdAndMajorId(Integer fromSemester, Integer toSemester, Integer majorId);
    String importExcelData(MultipartFile file);
    Subject findSubjectBySubjectCode(String subjectCode);
}
