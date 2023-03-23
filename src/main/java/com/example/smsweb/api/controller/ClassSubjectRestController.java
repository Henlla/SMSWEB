package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IClassSubject;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.ClassSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("api/class-subject")
public class ClassSubjectRestController extends GenericController<ClassSubject> {
    private final IClassSubject service;

    @Autowired
    public ClassSubjectRestController(IClassSubject service) {
        this.service = service;
    }

    @GetMapping("/findClassSubjectByClassIdAndSubjectId/{classId}/{subjectId}")
    ResponseEntity<?> findClassSubjectByClassIdAndSubjectId(@PathVariable("classId")int classId,
                                                            @PathVariable("subjectId")int subjectId){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(), service.findClassSubjectByClassIdAndSubjectId(classId, subjectId)));
    }
}
