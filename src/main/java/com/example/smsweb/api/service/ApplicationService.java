package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IApplication;
import com.example.smsweb.api.di.repository.ApplicationRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationService implements IApplication {
    @Autowired
    public ApplicationRepository dao;

    @Override
    public void save(Application application) {
        try {
            dao.save(application);
        }catch (Exception e){
            throw new ErrorHandler("Sao lưu thất bại");
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        }catch (Exception e){
            throw new ErrorHandler("Xóa thất bại");
        }
    }

    @Override
    public List<Application> findAll() {
        try {
            return dao.findAll();
        }catch (Exception e){
            throw new ErrorHandler("Không tìm thấy dữ liệu");
        }
    }

    @Override
    public Application findOne(int id) {
        try {
            return dao.findById(id).get();
        }catch (Exception e){
            throw new ErrorHandler("Không tìm thấy dữ liệu với id " + id);
        }
    }
}
