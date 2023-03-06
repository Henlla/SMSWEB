package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Integer> {
    Optional<Teacher> findTeacherByProfileId(Integer id);
    Optional<Teacher> findTeacherByTeacherCard(String card);
}
