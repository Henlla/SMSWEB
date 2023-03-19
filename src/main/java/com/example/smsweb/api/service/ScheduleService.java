package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.ISchedule;
import com.example.smsweb.api.di.repository.ScheduleRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService implements ISchedule {
    @Autowired
    ScheduleRepository dao;
    List<Schedule> listSchedule;

    @Override
    public void save(Schedule schedule) {
        try {
            dao.save(schedule);
        } catch (Exception e) {
            throw new ErrorHandler("Create faile");
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler("Delete fail");
        }
    }

    @Override
    public List<Schedule> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Get data fail");
        }
    }

    @Override
    public Schedule findOne(int id) {
        return dao.findById(id).orElseThrow(() -> new ErrorHandler("Don't find any data"));
    }

    @Override
    public Schedule saveSchedule(Schedule schedule) {
        try {
            Schedule scheduleSave = dao.save(schedule);
            return scheduleSave;
        } catch (Exception e) {
            throw new ErrorHandler("Create fail");
        }
    }

    @Override
    public Schedule findScheduleByClassAndSemester(Integer classId, Integer semester) {
        return dao.findScheduleAndByClassIdAndSemester(classId, semester).orElse(null);
    }

    @Override
    public Schedule findScheduleByClass(Integer classId) {
        return dao.findScheduleByClassId(classId).orElseThrow(() -> new ErrorHandler("Cannot find schedule with class id = " + classId));
    }

    @Override
    public List<Schedule> findScheduleByClassID(Integer classId) {
        return dao.findAllByClassId(classId).orElseThrow(() -> new ErrorHandler("Cannot find schedule list with classId = " + classId));
    }

    @Override
    public List<Schedule> findScheduleById(Integer scheduleId) {
        try {
            listSchedule = new ArrayList<>();
            listSchedule = dao.findSchedulesById(scheduleId);
            if (listSchedule.isEmpty()) {
                return null;
            } else {
                return listSchedule;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any data");
        }
    }
}
