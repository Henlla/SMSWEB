package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IClass;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Classses;
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
    public ResponseEntity<?> post(@ModelAttribute Classses newclass){
        try {
            service.save(newclass);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Sao lưu thành công", LocalTime.now().toString(),null));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Sao lưu thất bại", LocalTime.now().toString(),null));
        }
    }


    @GetMapping("/findClassId/{id}")
    public ResponseEntity<?> findByMajorId(@PathVariable("id") int id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(),service.findOne(id)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalTime.now().toString(),null));
        }
    }

}
