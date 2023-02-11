package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.ISubject;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("api/subject")
public class SubjectRestController extends GenericController<Subject> {
    @Autowired
    public ISubject service;

    @PostMapping("/save")
    public ResponseEntity<?> post(@ModelAttribute Subject subject){
        try {
            service.save(subject);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),"Sao lưu thành công"));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalTime.now().toString(),e.getMessage()));
        }
    }

    @GetMapping("/findByMajorId/{id}")
    public ResponseEntity<?> findByMajorId(@PathVariable("id") int id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),service.findSubjectByMajorId(id)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalTime.now().toString(),e.getMessage()));
        }
    }
}
