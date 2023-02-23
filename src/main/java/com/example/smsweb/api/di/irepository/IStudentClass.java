package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.StudentClass;

import java.util.List;

public interface IStudentClass extends IGenericRepository<StudentClass> {
    void saveAll(List<StudentClass> list);
}
