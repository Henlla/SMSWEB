package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends GenericRepository<Attendance, Integer> {
    Optional<List<Attendance>> findAttendancesByDate(String date);

    Optional<List<Attendance>> findAttendancesByDateAndSlot(String date, Integer slot);

    Attendance findAttendanceByDateAndSlotAndStudentSubjectId(String date, Integer slot, Integer studentSubjectId);

    List<Attendance> findAttendancesByStudentSubjectId(Integer studentSubjectId);

    List<Attendance> findAttendancesByDateAndSlotAndStudentSubjectId(String date, Integer slot, Integer studentSubjectId);

    List<Attendance> findAttendancesByDateAndSlotAndStudentSubjectIdAndShift(String date, Integer slot, Integer studentSubjectId, String shift);

    Attendance findAttendanceByDateAndSlotAndStudentSubjectIdAndShift(String date, Integer slot, Integer studentSubjectId, String shift);
}
