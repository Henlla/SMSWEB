package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.ISubject;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.models.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/subject")
public class SubjectRestController extends GenericController<Subject> {
    @Autowired
    public ISubject dao;

    @PostMapping("/save")
    public ResponseEntity<Object> post(@ModelAttribute Subject subject){
        try {
            dao.save(subject);
            return new ResponseEntity<>("Sao lưu thành công",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Sao lưu thất bại", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findByMajorId/{id}")
    public ResponseEntity<List<Subject>> findByMajorid(@PathVariable("id") int id){
        try {
            return new ResponseEntity<>(dao.findSubjectByMajorId(id),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
