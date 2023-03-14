package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.AttendanceTracking;

import java.util.List;

public interface AttendanceTrackingRepository extends GenericRepository<AttendanceTracking, Integer> {
    List<AttendanceTracking> findAttendanceTrackingsByTeacherId(Integer teacherId);

    List<AttendanceTracking> findAttendanceTrackingsByDateBetweenAndTeacherId(String fromDate, String toDate, Integer teacherId);
}
