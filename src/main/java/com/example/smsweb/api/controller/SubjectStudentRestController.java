package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.StudentSubjectService;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.StudentSubject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/student-subject")
public class SubjectStudentRestController {

    @Autowired
    private StudentSubjectService service;

    @PostMapping("/")
    public ResponseEntity<?> saveList(@RequestParam("student_subjectList") String studentSubjectList) throws JsonProcessingException {
        List<StudentSubject> list = new ObjectMapper().readValue(studentSubjectList, new TypeReference<List<StudentSubject>>() {
        });
        service.saveList(list);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(), list));
    }

    @PostMapping("/getOne")
    public ResponseEntity<?> getOne(@RequestParam("studentId") Integer studentId, @RequestParam("subjectId") Integer subjectId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findOneStudentSubject(studentId, subjectId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Error", LocalDate.now().toString(), e.getMessage()));
        }
    }

    @GetMapping("/getByAttendanceId/{id}")
    public ResponseEntity<?> getByAttendanceId(@PathVariable("id") String id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success",LocalDate.now().toString(),service.findByAttendanceStudentSubjectId(id)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error",LocalDate.now().toString(),e.getMessage()));
        }
    }
}
