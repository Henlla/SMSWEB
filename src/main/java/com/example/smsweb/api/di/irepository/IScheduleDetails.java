package com.example.smsweb.api.di.irepository;

import com.example.smsweb.models.ScheduleDetail;

import java.util.List;

public interface IScheduleDetails {

    public void addScheduleList(List<ScheduleDetail> list);
    List<ScheduleDetail> findScheduleDetailsByScheduleId(Integer id);
    ScheduleDetail findScheduleDetailById(Integer id);
}
