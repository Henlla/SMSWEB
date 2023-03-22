package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IClass;
import com.example.smsweb.api.di.repository.ClassRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Classses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassService implements IClass {
    private final ClassRepository repository;

    @Autowired
    public ClassService(ClassRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Classses classses) {
        repository.save(classses);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<Classses> findAll() {
        return repository.findAll();
    }

    @Override
    public Classses findOne(int id) {
        return repository.findById(id).orElseThrow(() -> new ErrorHandler("Cannot find class with id := " + id));
    }

    @Override
    public Classses findByClassCode(String classCode) {
        return repository.findClasssesByClassCode(classCode).orElseThrow(
                () -> new ErrorHandler("Cannot find class with class code := " + classCode));
    }

    @Override
    public List<String> searchClasssesByClassCode(String classCode) {
        List<String> stringList = repository.searchClasssesByClassCode(classCode);
        return stringList;
    }

    @Override
    public List<Classses> findClassByTeacherId(Integer id) {
        return repository.findAllByTeacherId(id);
    }

    @Override
    public Classses findClassByTeacherIdAndScheduleId(Integer teacherId, Integer scheduleId) {
        Optional<Classses> classsesOption = repository.findClasssesByTeacherIdAndId(teacherId, scheduleId);
        if (classsesOption.isPresent()) {
            return classsesOption.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Classses> findClassByMajorId(Integer majorId) {
        return repository.findAllByMajorId(majorId).orElseThrow(() -> new ErrorHandler("Cannot find class with majorId = " + majorId));
    }

    @Override
    public List<Classses> findClassByRoom(Integer roomId) {
        List<Classses> list = repository.findAllByRoomId(roomId);
        if(list.isEmpty()){
            return null;
        }
        return repository.findAllByRoomId(roomId);
    }


}
