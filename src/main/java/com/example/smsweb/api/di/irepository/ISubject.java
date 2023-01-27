package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Subject;

import java.util.List;

public interface ISubject extends IGenericRepository<Subject> {
    List<Subject> findSubjectByMajorId(int majorId);
}
