package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.ISubject;
import com.example.smsweb.api.di.repository.SubjectResponsitory;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService implements ISubject {
    @Autowired
    SubjectResponsitory dao;

    @Override
    public void save(Subject subject) {
        try {
            dao.save(subject);
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public List<Subject> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public Subject findOne(int id) {
        try {
            return dao.findById(id).get();
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public List<Subject> findSubjectByMajorId(int majorId) {
        try {
            return dao.findAllByMajorId(majorId);
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }
}
