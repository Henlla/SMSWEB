package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IStudent;
import com.example.smsweb.api.di.repository.StudentRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements IStudent {
    @Autowired
    private StudentRepository repository;
    @Override
    public void save(Student student) {
        repository.save(student);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public Student findOne(int id) {
        return repository.findById(id).orElseThrow(()->new ErrorHandler("Cannot find student with id := "+id));
    }

    @Override
    public Student getByProfileId(Integer id) {
        return repository.findStudentByProfileId(id).orElseThrow(()->new ErrorHandler("Cannot find student with profile id := "+id));
    }

    @Override
    public List<Student> findStudentIdByRangeStudentCard(List<String> listStudentCard) {
        List<Student> listResult= new ArrayList<>();
        for (String stringCard: listStudentCard) {
            Optional<Student> student = repository.findStudentByStudentCard(stringCard);
            if (student.isPresent())
                listResult.add(student.get());
            else throw new ErrorHandler("Không tìm thấy sinh viên mã: "+stringCard);
        }
        return listResult;
    }


}
