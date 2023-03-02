package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Student;

import java.util.List;

public interface IStudent extends IGenericRepository<Student> {
    Student getByProfileId(Integer id);
    Student findStudentByStudentCard(String studentCard);
    List<Student> findStudentIdByRangeStudentCard(List<String> listStudentCard);
}
