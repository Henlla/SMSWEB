package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Major;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;


@RestController
@RequestMapping("api/major")
public class MajorRestController extends GenericController<Major> {
    @Autowired
    private IMajor service;

    @PostMapping("/save")
    public ResponseEntity<?> save(@ModelAttribute Major major) {
        try {
            service.save(major);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Fail", LocalTime.now().toString(), e.getMessage()));
        }
    }
}
