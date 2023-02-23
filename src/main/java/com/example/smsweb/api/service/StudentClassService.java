package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IClass;
import com.example.smsweb.api.di.irepository.IStudentClass;
import com.example.smsweb.api.di.repository.ClassRepository;
import com.example.smsweb.api.di.repository.StudentClassRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.StudentClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentClassService implements IStudentClass {
    private final StudentClassRepository repository;

    @Autowired
    public StudentClassService(StudentClassRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(StudentClass studentClass) {
        repository.save(studentClass);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<StudentClass> findAll() {
        return repository.findAll();
    }

    @Override
    public StudentClass findOne(int id) {
        return repository.findById(id).orElseThrow(()->new ErrorHandler("Cannot find id := "+id));
    }

    @Override
    public void saveAll(List<StudentClass> list) {
        repository.saveAll(list);
    }
}