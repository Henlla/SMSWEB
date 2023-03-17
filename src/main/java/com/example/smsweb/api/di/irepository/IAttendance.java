package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Attendance;

import java.util.List;

public interface IAttendance extends IGenericRepository<Attendance> {
    void saveAllAttendance(List<Attendance> attendances);
    List<Attendance> findAttendByDate(String date);
    List<Attendance> findAttendanceByDateAndSlot(String date,Integer slot);
    Attendance findAttendanceByDateSlotStudentSubject(String date,String slot,String student_subject);
    List<Attendance> findAttendanceByStudentSubjectId(Integer studentSubjectId);
    List<Attendance> findAttendancesByDateAndSlotAndStudentSubjectId(String date,Integer slot, Integer studentSubjectId);
    List<Attendance> findAttendancesByDateAndSlotAndStudentSubjectIdAndShift(String date,Integer slot, Integer studentSubjectId, String shift);
    Attendance findAttendanceByDateAndSlotAndStudentSubjectIdAndShift(String date,Integer slot, Integer studentSubjectId, String shift);
}
