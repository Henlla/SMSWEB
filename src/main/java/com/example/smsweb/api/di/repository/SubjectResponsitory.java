package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectResponsitory extends GenericRepository<Subject, Integer> {
    List<Subject> findAllByMajorId(int majorId);

    Optional<List<Subject>> findAllByMajorIdAndSemesterId(Integer major, Integer semester);

    List<Subject> findSubjectsBySemesterIdIn(List<Integer> semesterId);

    List<Subject> findSubjectsBySemesterIdBetweenAndMajorId(Integer fromSemester, Integer toSemester, Integer majorId);

    Optional<Subject> findSubjectBySubjectCode(String subjectCode);

    List<Subject> findSubjectsBySubjectCode(String subjectCode);

}
