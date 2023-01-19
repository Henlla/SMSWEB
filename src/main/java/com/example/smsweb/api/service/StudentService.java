package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IStudent;
import com.example.smsweb.api.di.repository.StudentRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentService implements IStudent {
    @Autowired
    private StudentRepository repository;
    @Override
    public void save(Student student) {
        repository.save(student);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public Student findOne(int id) {
        return repository.findById(id).orElseThrow(()->new ErrorHandler("Cannot find student with id := "+id));
    }
}
