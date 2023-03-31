package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IApplication;
import com.example.smsweb.api.di.repository.ApplicationRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService implements IApplication {
    @Autowired
    public ApplicationRepository dao;
    List<Application> listApplication;

    @Override
    public void save(Application application) {
        try {
            dao.save(application);
        } catch (Exception e) {
            throw new ErrorHandler("Save fail");
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler("Delete fail");
        }
    }

    @Override
    public List<Application> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public Application findOne(int id) {
        return dao.findById(id).orElseThrow(() -> new ErrorHandler("Don't find any records"));
    }

    @Override
    public List<Application> findApplicationByStudentId(Integer id) {
        return dao.findApplicationsByStudentId(id);
    }

}
