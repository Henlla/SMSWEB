package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IApplication;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/application")
public class ApplicationRestController extends GenericController<Application> {
    @Autowired
    public IApplication service;
    List<Application> listApplication;

    @PostMapping(value = "/save")
    public ResponseEntity<?> post(@RequestParam("application") String applicationJson) {
        try {
            Application app = new ObjectMapper().readValue(applicationJson, Application.class);
            service.save(app);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Save success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Post fail"));
        }
    }

    @PutMapping(value = "/update")
    public ResponseEntity<?> update(@RequestParam("application") String applicationJson) {
        try {
            Application app = new ObjectMapper().readValue(applicationJson, Application.class);
            service.save(app);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Update success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Error", LocalDate.now().toString(), "Put fail"));
        }
    }

    @GetMapping("/finApplicationByStudentId/{id}")
    public ResponseEntity<?> findApplicationByStudentId(@PathVariable("id") Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findApplicationByStudentId(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Get fail"));
        }
    }
}
