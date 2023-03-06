package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectStudentRepository extends JpaRepository<StudentSubject,Integer> {
    Optional<StudentSubject> findStudentSubjectByStudentIdAndSubjectId(Integer studentId, Integer subjectId);
    Optional<StudentSubject> findStudentSubjectsById(String id);
}
