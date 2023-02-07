package com.example.smsweb.api.generic;

import com.example.smsweb.dto.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

public class GenericController<T> {
    @Autowired
    IGenericRepository<T> dao;

    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(), dao.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }

    @GetMapping("/findOne/{id}")
    public ResponseEntity<?> findOne(@PathVariable("id") Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(), dao.findOne(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {
        try {
            dao.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }
}
