package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Student;

public interface IStudent extends IGenericRepository<Student> {
    Student getByProfileId(Integer id);
}
