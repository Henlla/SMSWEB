package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.ITeacher;
import com.example.smsweb.api.di.repository.TeacherRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Teacher;
import org.hibernate.DuplicateMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService implements ITeacher {

    @Autowired
    private TeacherRepository repository;

    @Override
    public void save(Teacher teacher) {
        repository.save(teacher);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<Teacher> findAll() {
        return repository.findAll();
    }

    @Override
    public Teacher findOne(int id) {
        return repository.findById(id).orElseThrow(()->new ErrorHandler("Cannot find teacher with id := "+id));
    }

    @Override
    public Teacher findTeacherByProfileId(Integer id) {
        return repository.findTeacherByProfileId(id).orElseThrow(()->new ErrorHandler("Cannot find teacher with profileId := "+id));
    }

    @Override
    public Teacher findTeacherByCard(String card) {
        return repository.findTeacherByTeacherCard(card).orElseThrow(()->new ErrorHandler("Cannot find teacher with card = "+card));
    }
}
