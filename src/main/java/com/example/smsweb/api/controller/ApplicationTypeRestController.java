package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IApplicationType;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.ApplicationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("api/application_type")
public class ApplicationTypeRestController extends GenericController<ApplicationType> {
    @Autowired
    private IApplicationType service;

    @PostMapping("/save")
    public ResponseEntity<?> post(@ModelAttribute ApplicationType applicationType) {
        try {
            service.save(applicationType);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),"Sao lưu thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalTime.now().toString(),e.getMessage()));
        }
    }
}
