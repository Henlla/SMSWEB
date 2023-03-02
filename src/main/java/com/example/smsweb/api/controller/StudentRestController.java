package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IStudent;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentRestController {
    @Autowired
    private IStudent iStudent;
    @PostMapping("/")
    public ResponseEntity<?> saveStudent(@ModelAttribute Student student){
        iStudent.save(student);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(),student));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> findStudentById(@PathVariable("id") Integer id){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(), iStudent.findOne(id)));
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAllStudent(){
        return ResponseEntity.status(HttpStatus.OK).body(iStudent.findAll());
    }

    @GetMapping("/getByProfile/{id}")
    public ResponseEntity<?> findStudentByProfileId(@PathVariable("id")Integer id){
        return ResponseEntity.status(HttpStatus.OK).body(iStudent.getByProfileId(id));
    }

    @GetMapping("/findStudentByStudentCard/{studentCard}")
    public ResponseEntity<?> findStudentByStudentCard(@PathVariable("studentCard") String studentCard){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(iStudent.findStudentByStudentCard(studentCard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //find student id by range student card
    @GetMapping("/findStudentIdByRangeStudentCard/{listStudentCard}")
    public ResponseEntity<?> findStudentIdByRangeStudentCard(@PathVariable("listStudentCard") String listStudentCard){
        try {
            List<String> stringList = new ObjectMapper().readValue(listStudentCard, List.class);
            List<Student> studentIdByRangeStudentCard = iStudent.findStudentIdByRangeStudentCard(stringList);
            return ResponseEntity.status(HttpStatus.OK).body(studentIdByRangeStudentCard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
