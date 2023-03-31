package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.ClassSubject;
import com.example.smsweb.models.Semester;

public interface IClassSubject extends IGenericRepository<ClassSubject> {
    ClassSubject findClassSubjectByClassIdAndSubjectId(int classId, int subjectId);
}
