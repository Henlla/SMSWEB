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
        }catch (Exception ex){
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailsByScheduleId(Integer id) {
        return repository.findAllByScheduleId(id).orElseThrow(()->new ErrorHandler("Fail list schedule details by schedule id = "+id));
    }
}