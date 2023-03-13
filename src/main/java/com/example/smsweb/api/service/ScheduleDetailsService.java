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
    public List<ScheduleDetail> findScheduleDetailsByScheduleId(Integer id) {
        return repository.findAllByScheduleId(id).orElseThrow(() -> new ErrorHandler("Fail list schedule details by schedule id = " + id));
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
            listScheduleDetail = new ArrayList<>();
            listScheduleDetail = repository.findScheduleDetailByScheduleIdAndSubjectId(scheduleId, subjectId);
            if (listScheduleDetail.size() != 0) {
                return listScheduleDetail;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByDateBetween(String fromDate, String toDate) {
        try {
            listScheduleDetail = new ArrayList<>();
            listScheduleDetail = repository.findScheduleDetailsByDateBetween(fromDate, toDate);
            if (listScheduleDetail.size() != 0) {
                return listScheduleDetail;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<ScheduleDetail> findScheduleDetailByDateBetweenAndScheduleId(String fromDate, String toDate, Integer scheduleId) {
        try {
            listScheduleDetail = new ArrayList<>();
            listScheduleDetail = repository.findScheduleDetailsByDateBetweenAndScheduleId(fromDate, toDate, scheduleId);
            if (listScheduleDetail.size() == 0) {
                return null;
            } else {
                return listScheduleDetail;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
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
