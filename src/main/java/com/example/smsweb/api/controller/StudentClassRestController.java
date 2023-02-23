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
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Sao lưu thành công", LocalTime.now().toString(),newStudentClass));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Sao lưu thất bại", LocalTime.now().toString(),null));
        }
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestParam("listStudentClass") String listStudentClass) throws JsonProcessingException {
        try {
            List<StudentClass> list = new ObjectMapper().readValue(listStudentClass, new TypeReference<List<StudentClass>>(){});
            for (StudentClass item :list) {
                service.save(item);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Sao lưu thành công", LocalTime.now().toString(),list));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Sao lưu thất bại", LocalTime.now().toString(),null));
        }
    }

}