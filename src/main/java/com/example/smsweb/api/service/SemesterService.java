package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.ISemester;
import com.example.smsweb.api.di.repository.SemesterRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Semester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemesterService implements ISemester {
    @Autowired
    private SemesterRepository dao;

    @Override
    public void save(Semester semester) {
        try {
            dao.save(semester);
        } catch (Exception e) {
            throw new ErrorHandler("Sao lưu thất bại");
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy id : " + id + " để xóa");
        }
    }

    @Override
    public List<Semester> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Không có dữ liệu");
        }
    }

    @Override
    public Semester findOne(int id) {
        try {
            return dao.findById(id).get();
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy dữ liệu có id : " + id);
        }
    }
}
