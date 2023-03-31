package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends GenericRepository<Schedule, Integer> {
    Optional<Schedule> findScheduleAndByClassIdAndSemester(Integer classId, Integer semester);

    Optional<Schedule> findScheduleByClassId(Integer classId);

    Optional<List<Schedule>> findAllByClassId(Integer classId);

    List<Schedule> findSchedulesById(Integer scheduleId);
}
