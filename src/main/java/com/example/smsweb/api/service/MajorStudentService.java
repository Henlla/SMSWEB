package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IStudentMajor;
import com.example.smsweb.api.di.repository.StudentMajorRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.MajorStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MajorStudentService implements IStudentMajor {
    @Autowired
    private StudentMajorRepository repository;

    public void save(MajorStudent list) {
        repository.save(list);
    }

    @Override
    public MajorStudent findMajorStudentByStudentId(Integer studentId) {
        try {
            return repository.findMajorStudentByStudentId(studentId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }
}
