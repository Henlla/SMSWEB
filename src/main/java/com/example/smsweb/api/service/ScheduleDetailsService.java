package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IScheduleDetails;
import com.example.smsweb.api.di.repository.ScheduleDetailsRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.ScheduleDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ScheduleDetailsService implements IScheduleDetails {

    @Autowired
    private ScheduleDetailsRepository repository;

    List<ScheduleDetail> listScheduleDetail;

    @Override
    public void addScheduleList(List<ScheduleDetail> list) {
        try {
            repository.saveAll(list);
        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleByDate(String date) {
        return repository.findByDate(date).orElseThrow(() -> new ErrorHandler("Don't find records with date: " + date));
    }

    @Override
    public List<ScheduleDetail> findScheduleDetail(String date, String scheduleId) {
        return repository.findScheduleDetailByDateAndScheduleId(date, scheduleId).orElseThrow(() -> new ErrorHandler("Don't find record"));
    }

    public ScheduleDetail findScheduleDetailById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ErrorHandler("Cannot find schedule details with id = " + id));
    }

    @Override
    public ScheduleDetail findScheduleDetailBySlot(String date, String scheduleId, String slot) {
        return repository.findScheduleDetailByDateAndScheduleIdAndSlot(date, scheduleId, slot).orElseThrow(() -> new ErrorHandler("Don't find any records"));
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByScheduleIdAndSubjectId(Integer scheduleId, Integer subjectId) {
        try {
            return repository.findScheduleDetailByScheduleIdAndSubjectId(scheduleId, subjectId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByDateBetween(String fromDate, String toDate) {
        try {
            return repository.findScheduleDetailsByDateBetween(fromDate, toDate);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByDateBetweenAndScheduleId(String fromDate, String toDate, Integer scheduleId) {
        try {
            return repository.findScheduleDetailsByDateBetweenAndScheduleId(fromDate, toDate, scheduleId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByDateBetweenAndScheduleIdAndTeacherId(String fromDate, String toDate, Integer scheduleId, Integer teacherId) {
        try {
            return repository.findScheduleDetailsByDateBetweenAndScheduleIdAndTeacherId(fromDate, toDate, scheduleId, teacherId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailsByDateBetweenAndTeacherId(String fromDate, String toDate, Integer teacherId) {
        try {
            return repository.findScheduleDetailsByDateBetweenAndTeacherId(fromDate, toDate, teacherId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailsByDateAndTeacherId(String date, Integer teacherId) {
        try {
            return repository.findScheduleDetailsByDateAndTeacherId(date, teacherId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find nany records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailsByDateAndScheduleIdAndTeacherId(String date, Integer scheduleId, Integer teacherId) {
        try {
            return repository.findScheduleDetailsByDateAndScheduleIdAndTeacherId(date, scheduleId, teacherId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByDateSlotAndShift(String date, Integer slot, String shift) {
        try {
            return repository.findScheduleDetailsByDateAndSlotAndShift(date, slot, shift);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByTeacher(Integer teacherId) {
        return repository.findAllByTeacherId(teacherId);
    }

    public void putScheduleDetails(ScheduleDetail scheduleDetail) {
        try {
            repository.save(scheduleDetail);
        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    public ScheduleDetail findScheduleDetailsByDate(String date) {
        return repository.findScheduleDetailByDate(date).orElseThrow(() -> new ErrorHandler("Cannot find schedule detail with date = " + date));
    }
}
