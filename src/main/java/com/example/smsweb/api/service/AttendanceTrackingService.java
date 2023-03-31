package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IAttendanceTracking;
import com.example.smsweb.api.di.repository.AttendanceTrackingRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.AttendanceTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceTrackingService implements IAttendanceTracking {
    @Autowired
    AttendanceTrackingRepository dao;
    List<AttendanceTracking> attendanceTrackingList;

    @Override
    public void save(AttendanceTracking attendanceTracking) {
        dao.save(attendanceTracking);
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<AttendanceTracking> findAll() {
        return null;
    }

    @Override
    public AttendanceTracking findOne(int id) {
        return null;
    }

    @Override
    public List<AttendanceTracking> findByTeacherId(Integer teacherId) {
        try {
            return dao.findAttendanceTrackingsByTeacherId(teacherId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<AttendanceTracking> findByDateBetweenAndTeacherId(String fromDate, String toDate, Integer teacherId) {
        try {
            return dao.findAttendanceTrackingsByDateBetweenAndTeacherId(fromDate, toDate, teacherId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }
}
