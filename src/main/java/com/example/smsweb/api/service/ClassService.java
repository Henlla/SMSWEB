package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IClass;
import com.example.smsweb.api.di.repository.ClassRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Classses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService implements IClass {
    private final ClassRepository repository;

    @Autowired
    public ClassService(ClassRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Classses classses) {
        repository.save(classses);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<Classses> findAll() {
        return repository.findAll();
    }

    @Override
    public Classses findOne(int id) {
        return repository.findById(id).orElseThrow(() -> new ErrorHandler("Cannot find class with id := "+id));
    }

    @Override
    public Classses findByClassCode(String classCode) {
        return repository.findClasssesByClassCode(classCode).orElseThrow(
                () -> new ErrorHandler("Cannot find class with class code := "+classCode));
    }
}