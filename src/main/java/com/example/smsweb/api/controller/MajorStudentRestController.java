package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.MajorStudentService;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.MajorStudent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("api/student-major")
public class MajorStudentRestController {

    @Autowired
    private MajorStudentService service;

    @PostMapping("/")
    public ResponseEntity<?> saveList(@RequestParam("student_major") String studentMajor) throws JsonProcessingException {
        MajorStudent majorStudent = new ObjectMapper().readValue(studentMajor, new TypeReference<MajorStudent>() {
        });
        service.save(majorStudent);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(), majorStudent));
    }

    @GetMapping("/findMajorStudentByStudentId/{studentId}")
    public ResponseEntity<?> findMajorStudentByStudentId(@PathVariable("studentId") Integer studentId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findMajorStudentByStudentId(studentId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }
}
