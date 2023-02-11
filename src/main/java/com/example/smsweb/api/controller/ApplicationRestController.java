package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IApplication;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("api/application")
public class ApplicationRestController extends GenericController<Application> {
    @Autowired
    public IApplication service;

    @PostMapping("/save")
    public ResponseEntity<?> post(@ModelAttribute Application application) {
        try {
            service.save(application);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(), "Sao lưu thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalTime.now().toString(), e.getMessage()));
        }
    }
}
