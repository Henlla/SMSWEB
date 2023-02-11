package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IApplicationType;
import com.example.smsweb.api.di.repository.ApplicatiionTypeRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.ApplicationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ApplicationTypeService implements IApplicationType {
    @Autowired
    private ApplicatiionTypeRepository dao;

    @Override
    public void save(ApplicationType applicationType) {
        try {
            dao.save(applicationType);
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public List<ApplicationType> findAll() {
        try {
            return dao.findAll();
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public ApplicationType findOne(int id) {
        try {
            return dao.findById(id).get();
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }
}
