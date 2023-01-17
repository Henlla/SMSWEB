package com.example.smsweb.api.generic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class GenericController<T> {
    @Autowired
    IGenericRepository<T> dao;

    @GetMapping("/list")
    public ResponseEntity<List<T>> findall(){
        return new ResponseEntity<List<T>>(dao.findAll(), HttpStatus.OK);
    }

    
}
