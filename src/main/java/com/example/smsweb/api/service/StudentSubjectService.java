package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.StudentClassRepository;
import com.example.smsweb.api.di.repository.SubjectStudentRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.StudentSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StudentSubjectService {
    @Autowired
    private StudentClassRepository studentClassRepository;
    @Autowired
    private SubjectStudentRepository repository;
    List<StudentSubject> listStudentSubject;

    public void saveList(List<StudentSubject> list) {
        repository.saveAll(list);
    }

    public StudentSubject findOneStudentSubject(Integer studentId, Integer subjectId) {
        return repository.findStudentSubjectByStudentIdAndSubjectId(studentId, subjectId).orElseThrow(() -> new ErrorHandler("Don't find any record"));
    }

    public StudentSubject findByAttendanceStudentSubjectId(String id) {
        return repository.findStudentSubjectsById(id).orElseThrow(() -> new ErrorHandler("Don't find student subject with id = " + id));
    }

    public List<StudentSubject> findStudentSubjectByStudentId(Integer id) {
        try {
            listStudentSubject = new ArrayList<>();
            listStudentSubject = repository.findStudentSubjectByStudentId(id);
            if (listStudentSubject.size() != 0) {
                return listStudentSubject;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't find any records");
        }
    }

    public StudentSubject findStudentSubjectById(Integer id) {
        try {
            StudentSubject studentSubject = new StudentSubject();
            studentSubject = repository.findStudentSubjectById(id);
            if (studentSubject != null) {
                return studentSubject;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't find any records");
        }
    }

    public StudentSubject findStudentSubjectBySubjectIdAndStudentId(Integer subjectId, Integer studentId) {
        try {
            StudentSubject studentSubject = new StudentSubject();
            studentSubject = repository.findStudentSubjectBySubjectIdAndStudentId(subjectId, studentId);
            if (studentSubject != null) {
                return studentSubject;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    public void update(StudentSubject studentSubject) {
        try {
            repository.save(studentSubject);
        }catch (Exception e){
            throw new ErrorHandler("Fail");
        }
    }

    public void updateAll(List<StudentSubject> listStudentSubject) {
        try {
            repository.saveAll(listStudentSubject);
        }catch (Exception e){
            throw new ErrorHandler("Fail");
        }
    }
}
