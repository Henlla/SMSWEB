package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IApplicationType;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.ApplicationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/application_type")
public class ApplicationTypeRestController extends GenericController<ApplicationType> {
    @Autowired
    private IApplicationType service;

    @PostMapping("/save")
    public ResponseEntity<?> post(@RequestParam("applicationType") String applicationTypeJson) {
        try {
            ApplicationType applicationType = new ObjectMapper().readValue(applicationTypeJson, ApplicationType.class);
            service.save(applicationType);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Save success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Save fail"));
        }
    }
}
