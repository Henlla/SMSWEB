package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IClass;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Classses;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/classes")
public class ClassRestController extends GenericController<Classses> {
    @Autowired
    public IClass service;

    @Override
    public ResponseEntity<?> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @PostMapping("/save")
    public ResponseEntity<?> post(@RequestParam("newClass") String newclass){
        try {
            Classses classses = new ObjectMapper().readValue(newclass, Classses.class);
            service.save(classses);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Sao lưu thành công", LocalTime.now().toString(),newclass));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Sao lưu thất bại", LocalTime.now().toString(),null));
        }
    }


    @PostMapping("/findClassCode")
    public ResponseEntity<?> findClassCode(@RequestParam("classCode") String classCode){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Sao lưu thành công", LocalTime.now().toString(),service.findByClassCode(classCode)));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Sao lưu thất bại", LocalTime.now().toString(),null));
        }
    }

}
