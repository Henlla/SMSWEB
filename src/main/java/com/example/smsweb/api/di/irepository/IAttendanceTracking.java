package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.AttendanceTracking;

import java.util.List;

public interface IAttendanceTracking extends IGenericRepository<AttendanceTracking> {
    List<AttendanceTracking> findByTeacherId(Integer teacherId);

    List<AttendanceTracking> findByDateBetweenAndTeacherId(String fromDate, String toDate, Integer teacherId);
}
