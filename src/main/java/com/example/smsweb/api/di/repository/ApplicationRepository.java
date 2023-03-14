package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Application;

import java.util.List;

public interface ApplicationRepository extends GenericRepository<Application, Integer> {
    List<Application> findApplicationsByStudentId(Integer studentId);
}
