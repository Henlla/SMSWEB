package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.StudentClass;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentClassRepository extends GenericRepository<StudentClass, Integer> {
    Optional<List<StudentClass>> findAllByStudentId(Integer id);
    Optional<List<StudentClass>> findAllByClassId(Integer id);
    StudentClass findStudentClassByStudentId(Integer id);
    Optional<List<StudentClass>> findStudentClassesByStudentId(Integer id);
    StudentClass findStudentClassByClassIdAndStudentId(Integer classId,Integer studentId);
}
