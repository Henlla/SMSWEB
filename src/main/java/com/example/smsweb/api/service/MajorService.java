package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Major;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MajorService implements IMajor {
    @Autowired
    private MajorRepository dao;

    @Override
    public void save(Major major) {
        try {
            dao.save(major);
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
    public List<Major> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public Major findOne(int id) {
        try {
            return dao.findById(id).get();
        } catch (Exception e) {
            throw new ErrorHandler("Don't find Major with id: " + id);
        }
    }
}
