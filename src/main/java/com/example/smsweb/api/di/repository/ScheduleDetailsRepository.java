package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.ScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleDetailsRepository extends JpaRepository<ScheduleDetail, Integer> {
    Optional<List<ScheduleDetail>> findAllByScheduleId(Integer id);

    Optional<List<ScheduleDetail>> findByDate(String date);

    Optional<List<ScheduleDetail>> findScheduleDetailByDateAndScheduleId(String date, String scheduleId);

    Optional<ScheduleDetail> findScheduleDetailByDate(String date);

    Optional<ScheduleDetail> findScheduleDetailByDateAndScheduleIdAndSlot(String date, String scheduleId, String slot);

    List<ScheduleDetail> findScheduleDetailByScheduleIdAndSubjectId(Integer scheduleId, Integer subjectId);

    List<ScheduleDetail> findScheduleDetailsByDateBetween(String fromDate, String toDate);

    List<ScheduleDetail> findScheduleDetailsByDateBetweenAndScheduleId(String fromdate, String toDate, Integer scheduleId);

    List<ScheduleDetail> findAllByTeacherId(Integer teacher);
}
