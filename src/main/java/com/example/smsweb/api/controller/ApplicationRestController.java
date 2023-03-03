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

@RestController
@RequestMapping("api/application")
public class ApplicationRestController extends GenericController<Application> {
    @Autowired
    public IApplication service;

    @PostMapping(value = "/save")
    public ResponseEntity<?> post(@RequestParam("application") String applicationJson) {
        try {
            Application app = new ObjectMapper().readValue(applicationJson, Application.class);
            service.save(app);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Sao lưu thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), e.getMessage()));
        }
    }

    @PutMapping(value = "/update")
    public ResponseEntity<?> update(@RequestParam("application") String applicationJson) {
        try {
            Application app = new ObjectMapper().readValue(applicationJson, Application.class);
            service.save(app);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Cập nhật thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Error", LocalDate.now().toString(), "Cập nhật thất bại"));
        }
    }
}
