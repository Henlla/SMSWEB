package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.StudentClass;

import java.util.List;

public interface IStudentClass extends IGenericRepository<StudentClass> {
    void saveAll(List<StudentClass> list);

    List<StudentClass> findClassIdByStudentId(Integer id);

    List<StudentClass> findStudentByClassId(Integer id);

    StudentClass findClassByStudent(Integer id);
    List<StudentClass> findStudentClassesByStudentId(int id);

    List<StudentClass> findClassesByStudentId(Integer id);
}
