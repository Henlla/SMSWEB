package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.ClassSubject;
import com.example.smsweb.models.Semester;

import java.util.Optional;

public interface ClassSubjectRepository extends GenericRepository<ClassSubject,Integer> {
    Optional<ClassSubject> findClassSubjectByClassIdAndSubjectId(int classId, int subjectId);
}
