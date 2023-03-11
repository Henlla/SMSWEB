package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IStudentClass;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.StudentClass;
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
@RequestMapping("api/student-class/")
public class StudentClassRestController extends GenericController<StudentClass> {
    @Autowired
    public IStudentClass service;

    @PostMapping("/save")
    public ResponseEntity<?> post(@RequestParam("newStudentClass") String newStudentClass){
        try {
            StudentClass studentClass = new ObjectMapper().readValue(newStudentClass, StudentClass.class);
            service.save(studentClass);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),newStudentClass));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalTime.now().toString(),"Save fail"));
        }
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestParam("listStudentClass") String listStudentClass) throws JsonProcessingException {
        try {
            List<StudentClass> list = new ObjectMapper().readValue(listStudentClass, new TypeReference<List<StudentClass>>(){});
            for (StudentClass item :list) {
                service.save(item);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),list));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalTime.now().toString(),"Save fail"));
        }
    }

    @GetMapping("/getStudent/{id}")
    public ResponseEntity<?> getStudent(@PathVariable("id")Integer id) throws JsonProcessingException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),service.findClassIdByStudentId(id)));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalTime.now().toString(),"Don't find any records"));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllStudentClass() throws JsonProcessingException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),service.findAll()));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalTime.now().toString(),"Don't find any records"));
        }
    }

    @GetMapping("/getStudentByClassCode/{id}")
    public ResponseEntity<?> findStudentByClassId(@PathVariable("id") Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findStudentByClassId(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @GetMapping("/getClassByStudentId/{id}")
    public ResponseEntity<?> findClassByStudent(@PathVariable("id")Integer id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success",LocalDate.now().toString(),service.findClassByStudent(id)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error",LocalDate.now().toString(),"Don't find any records"));
        }
    }
}
