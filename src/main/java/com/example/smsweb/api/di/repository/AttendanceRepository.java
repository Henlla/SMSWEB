package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends GenericRepository<Attendance,Integer> {
    Optional<List<Attendance>> findAttendancesByDate(String date);
}
