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
    public ResponseEntity<List<T>> findAll() {
        try {
            return new ResponseEntity<>(dao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findOne/{id}")
    public ResponseEntity<T> findOne(@PathVariable("id") Integer id) {
        try {
            return new ResponseEntity<>(dao.findOne(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {
        try {
            dao.delete(id);
            return new ResponseEntity<>("Xóa thành công", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Xóa thất bại", HttpStatus.BAD_REQUEST);
        }
    }
}
