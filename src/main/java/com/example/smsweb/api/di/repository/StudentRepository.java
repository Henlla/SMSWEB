package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Student;

import java.util.Optional;

public interface StudentRepository extends GenericRepository<Student,Integer> {
    Optional<Student> findStudentByProfileId(Integer integer);

}
