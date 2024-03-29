package com.example.smsweb.api.di.irepository;

import com.example.smsweb.models.ScheduleDetail;

import java.util.List;

public interface IScheduleDetails {

    void addScheduleList(List<ScheduleDetail> list);

    List<ScheduleDetail> findScheduleByDate(String date);
    List<ScheduleDetail> findSchedulesOrNullByDate(String date);

    List<ScheduleDetail> findScheduleDetail(String date, String scheduleId);

    ScheduleDetail findScheduleDetailById(Integer id);

    ScheduleDetail findScheduleDetailBySlot(String date, String scheduleId, String slot);

    List<ScheduleDetail> findScheduleDetailByScheduleIdAndSubjectId(Integer scheduleId, Integer subjectId);

    List<ScheduleDetail> findScheduleDetailByDateBetween(String fromDate, String toDate);

    List<ScheduleDetail> findScheduleDetailByDateBetweenAndScheduleId(String fromDate, String toDate, Integer scheduleId);

    List<ScheduleDetail> findScheduleDetailByTeacher(Integer teacherId);


    List<ScheduleDetail> findScheduleDetailByDateBetweenAndScheduleIdAndTeacherId(String fromDate, String toDate, Integer scheduleId, Integer teacherId);

    List<ScheduleDetail> findScheduleDetailsByDateBetweenAndTeacherId(String fromDate, String toDate, Integer teacherId);

    List<ScheduleDetail> findScheduleDetailsByDateAndTeacherId(String date, Integer teacherId);

    List<ScheduleDetail> findScheduleDetailsByDateAndScheduleIdAndTeacherId(String date, Integer scheduleId, Integer teacherId);

    List<ScheduleDetail> findScheduleDetailByDateSlotAndShift(String date, Integer slot, String shift);
}
