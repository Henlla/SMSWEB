package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Schedule;

public interface ISchedule extends IGenericRepository<Schedule> {
    Schedule saveSchedule(Schedule schedule);
    Schedule findScheduleByClassAndSemester(Integer classId,Integer semester);
    Schedule findScheduleByClass(Integer classId);
}
