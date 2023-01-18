package com.example.smsweb.api.generic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class GenericController<T> {
    @Autowired
    IGenericRepository<T> dao;

    @GetMapping("/list")
    public ResponseEntity<List<T>> findAll(){
        try {
            return new ResponseEntity<>(dao.findAll(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/findOne/{id}")
    public ResponseEntity<T> findOne(@PathVariable("id") int id){
        try {
            return new ResponseEntity<>(dao.findOne(id),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/post")
    public ResponseEntity<Object> post (@RequestBody T t){
        try {
            dao.save(t);
            return new ResponseEntity<>("Post success",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Post fail",HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody T t){
        try {
            dao.save(t);
            return new ResponseEntity<>("Update success",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Update fail",HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") int id){
        try {
            dao.delete(id);
            return new ResponseEntity<>("Delete Success",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Delete fail",HttpStatus.BAD_REQUEST);
        }
    }
}
