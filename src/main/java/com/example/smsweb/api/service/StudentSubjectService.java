package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.SubjectStudentRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.StudentSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentSubjectService {
    @Autowired
    private SubjectStudentRepository repository;

    public void saveList(List<StudentSubject> list) {
        repository.saveAll(list);
    }

    public StudentSubject findOneStudentSubject(Integer studentId, Integer subjectId) {
        return repository.findStudentSubjectByStudentIdAndSubjectId(studentId, subjectId).orElseThrow(() -> new ErrorHandler("Không tìm thấy student subject"));
    }

    public StudentSubject findByAttendanceStudentSubjectId(String id) {
        return repository.findStudentSubjectsById(id).orElseThrow(() -> new ErrorHandler("Không tìm thấy student subject id = " + id));
    }
}
