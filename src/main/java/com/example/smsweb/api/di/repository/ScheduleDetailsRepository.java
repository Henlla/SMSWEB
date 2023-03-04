package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.Schedule;
import com.example.smsweb.models.ScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleDetailsRepository extends JpaRepository<ScheduleDetail,Integer> {
    Optional<List<ScheduleDetail>> findAllByScheduleId(Integer id);
    Optional<ScheduleDetail> findScheduleDetailByDate(String date);
}
