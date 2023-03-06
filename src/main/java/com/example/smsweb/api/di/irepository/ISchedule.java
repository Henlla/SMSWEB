package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Schedule;

import java.util.List;

public interface ISchedule extends IGenericRepository<Schedule> {
    Schedule saveSchedule(Schedule schedule);
    Schedule findScheduleByClassAndSemester(Integer classId,Integer semester);
    Schedule findScheduleByClass(Integer classId);
    List<Schedule> findScheduleByClassID(Integer classId);
}
