package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IStudent;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/students")
public class StudentRestController extends GenericController<Student> {
    @Autowired
    private IStudent iStudent;
    @PostMapping("/")
    public ResponseEntity<?> saveStudent(@ModelAttribute Student student){
        iStudent.save(student);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(),student));
    }
}
