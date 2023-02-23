package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.StudentMajorRepository;
import com.example.smsweb.api.di.repository.SubjectStudentRepository;
import com.example.smsweb.models.MajorStudent;
import com.example.smsweb.models.StudentSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorStudentService {
    @Autowired
    private StudentMajorRepository repository;

    public void save(MajorStudent list){
        repository.save(list);
    }
}
