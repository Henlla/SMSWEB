package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.StudentClass;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentClassRepository extends GenericRepository<StudentClass, Integer> {
}
