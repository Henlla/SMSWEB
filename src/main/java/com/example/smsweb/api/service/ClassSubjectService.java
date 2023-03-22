package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IClassSubject;
import com.example.smsweb.api.di.repository.ClassSubjectRepository;
import com.example.smsweb.api.di.repository.SemesterRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.ClassSubject;
import com.example.smsweb.models.Semester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassSubjectService implements IClassSubject {
    @Autowired
    private ClassSubjectRepository repository;

    @Override
    public ClassSubject findClassSubjectByClassIdAndSubjectId(int classId, int subjectId) {
        return repository.findClassSubjectByClassIdAndSubjectId(classId, subjectId).orElse(null);
    }

    @Override
    public void save(ClassSubject classSubject) {
        repository.save(classSubject);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<ClassSubject> findAll() {
        return repository.findAll();
    }

    @Override
    public ClassSubject findOne(int id) {
        return repository.findById(id).orElse(null);
    }
}
