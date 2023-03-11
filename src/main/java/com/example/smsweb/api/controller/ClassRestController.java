package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IClass;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Classses;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/classes")
public class ClassRestController extends GenericController<Classses> {
    @Autowired
    public IClass service;

    @Override
    public ResponseEntity<?> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @PostMapping("/save")
    public ResponseEntity<?> post(@RequestParam("newClass") String newclass) {
        try {
            Classses classses = new ObjectMapper().readValue(newclass, new TypeReference<Classses>() {
            });
            service.save(classses);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Sao lưu thành công", LocalTime.now().toString(), newclass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Sao lưu thất bại", LocalTime.now().toString(), e.getMessage()));
        }
    }

    @PostMapping("/findClassCode")
    public ResponseEntity<?> findClassCode(@RequestParam("classCode") String classCode) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Sao lưu thành công", LocalTime.now().toString(), service.findByClassCode(classCode)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Sao lưu thất bại", LocalTime.now().toString(), null));
        }
    }

    @GetMapping("/searchClasssesByClassCode/{classCode}")
    public List<String> searchClasssesByClassCode(@PathVariable("classCode") String classCode) {
        classCode = "%" + classCode + "%";
        return service.searchClasssesByClassCode(classCode);
    }

    @GetMapping("/getClass/{id}")
    public ResponseEntity<?> getClass(@PathVariable("id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Tìm thành công", LocalTime.now().toString(), service.findOne(id)));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @GetMapping("/findClassByTeacher/{id}")
    public ResponseEntity<?> findClassByTeacher(@PathVariable("id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Tìm thành công", LocalTime.now().toString(), service.findClassByTeacherId(id)));
    }

    @PostMapping("/findClassByTeacherAndSchedule")
    public ResponseEntity<?> findClassByTeacherAndSchedule(@RequestParam("teacherId") String teacherId, @RequestParam("scheduleId") String scheduleId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findClassByTeacherIdAndScheduleId(teacherId, scheduleId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), e.getMessage()));
        }
    }

    @PutMapping("/updateClass")
    public ResponseEntity<?> updateClass(@RequestParam("class") String classes) throws JsonProcessingException {
        Classses classses = new ObjectMapper().readValue(classes, Classses.class);
        service.save(classses);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
