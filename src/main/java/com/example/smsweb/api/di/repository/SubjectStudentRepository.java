package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectStudentRepository extends JpaRepository<StudentSubject,Integer> {
}
