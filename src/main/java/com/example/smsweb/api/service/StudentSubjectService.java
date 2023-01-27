package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.SubjectStudentRepository;
import com.example.smsweb.models.StudentSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentSubjectService {
    @Autowired
    private SubjectStudentRepository repository;

    public void saveList(List<StudentSubject> list){
        repository.saveAll(list);
    }
}
