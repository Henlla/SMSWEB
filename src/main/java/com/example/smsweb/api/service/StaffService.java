package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IStaff;
import com.example.smsweb.api.di.repository.StaffRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService implements IStaff {

    @Autowired
    StaffRepository repository;
    @Override
    public void save(Staff staff) {
        try {
            repository.save(staff);
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            repository.deleteById(id);
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }

    }

    @Override
    public List<Staff> findAll() {
        try {
            return repository.findAll();
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public Staff findOne(int id) {
        return  repository.findById(id).orElseThrow(()-> new ErrorHandler("Cannot find staff with id = "+id));
    }
}
