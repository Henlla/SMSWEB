package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IScheduleDetails;
import com.example.smsweb.api.di.repository.ScheduleDetailsRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.ScheduleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleDetailsService implements IScheduleDetails {

    @Autowired
    private ScheduleDetailsRepository repository;

    @Override
    public void addScheduleList(List<ScheduleDetail> list) {
        try {
            repository.saveAll(list);
        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailsByScheduleId(Integer id) {
        return repository.findAllByScheduleId(id).orElseThrow(() -> new ErrorHandler("Fail list schedule details by schedule id = " + id));
    }

    @Override
    public List<ScheduleDetail> findScheduleByDate(String date) {
        return repository.findByDate(date).orElseThrow(() -> new ErrorHandler("Không tìm thấy schedule với date: " + date));
    }

    @Override
    public ScheduleDetail findScheduleDetail(String date, String scheduleId) {
        return repository.findScheduleDetailByDateAndScheduleId(date,scheduleId).orElseThrow(()-> new ErrorHandler("Không tìm thấy schedule detail với id = " + scheduleId));
    }

    public void putScheduleDetails(ScheduleDetail scheduleDetail) {
        try {
            repository.save(scheduleDetail);
        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }
}
