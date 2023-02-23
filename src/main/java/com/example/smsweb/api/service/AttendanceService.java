package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IAttendance;
import com.example.smsweb.api.di.repository.AttendanceRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService implements IAttendance {
    @Autowired
    private AttendanceRepository dao;

    @Override
    public void save(Attendance attendance) {
        try {
            dao.save(attendance);
        } catch (Exception e) {
            throw new ErrorHandler("Sao lưu thất bại");
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler("Xóa thất bại");
        }
    }

    @Override
    public List<Attendance> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy dữ liệu Attendance");
        }
    }

    @Override
    public Attendance findOne(int id) {
        try {
            return dao.findById(id).get();
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy id Attendance " + id);
        }
    }
}
