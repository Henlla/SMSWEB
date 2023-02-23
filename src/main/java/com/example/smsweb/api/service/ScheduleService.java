package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.ISchedule;
import com.example.smsweb.api.di.repository.ScheduleRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Schedule;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ScheduleService implements ISchedule {
    @Autowired
    ScheduleRepository dao;
    @Override
    public void save(Schedule schedule) {
        try {
            dao.save(schedule);
        }catch (Exception e){
            throw new ErrorHandler("Tạo thất bại");
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
    public List<Schedule> findAll() {
        try {
            return dao.findAll();
        }catch (Exception e){
            throw new ErrorHandler("Lấy dữ liệu thất bại");
        }
    }

    @Override
    public Schedule findOne(int id) {
           return dao.findById(id).orElseThrow(()-> new ErrorHandler("Không tìm thấy dữ liệu"));
    }
}
