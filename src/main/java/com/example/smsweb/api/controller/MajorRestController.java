package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Major;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("api/major")
public class MajorRestController extends GenericController<Major> {
    @Autowired
    private IMajor service;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestParam("major") String jsonMajor) {
        try {
            Major major = new ObjectMapper().readValue(jsonMajor,Major.class);
            service.save(major);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Create success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Create fail"));
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestParam("major") String jsonMajor){
        try {
            Major major = new ObjectMapper().readValue(jsonMajor, Major.class);
            service.save(major);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Update success"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(),"Update fail"));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> findMajorById(@PathVariable("id") Integer majorId){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findOne(majorId)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(),"Don't find any records"));
        }
    }
}
