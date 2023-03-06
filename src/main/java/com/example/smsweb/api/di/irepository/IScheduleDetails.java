package com.example.smsweb.api.di.irepository;

import com.example.smsweb.models.ScheduleDetail;

import java.util.List;

public interface IScheduleDetails {

    void addScheduleList(List<ScheduleDetail> list);
    List<ScheduleDetail> findScheduleDetailsByScheduleId(Integer id);
    List<ScheduleDetail> findScheduleByDate(String date);
    ScheduleDetail findScheduleDetail(String date,String scheduleId);
    ScheduleDetail findScheduleDetailById(Integer id);
}
