package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Attendance;

import java.util.List;

public interface IAttendance extends IGenericRepository<Attendance> {
    void saveAllAttendance(List<Attendance> attendances);
    List<Attendance> findAttendByDate(String date);
}
