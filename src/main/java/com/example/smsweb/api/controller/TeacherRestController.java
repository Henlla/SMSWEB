package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.ITeacher;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Student;
import com.example.smsweb.models.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/teachers")
public class TeacherRestController {
    @Autowired
    private ITeacher iTeacher;
    @PostMapping("/")
    public ResponseEntity<?> saveTeacher(@ModelAttribute Teacher teacher){
        iTeacher.save(teacher);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(),teacher));
    }
    @GetMapping("/list")
    public ResponseEntity<?> findAllTeacher(){
        return ResponseEntity.status(HttpStatus.OK).body(iTeacher.findAll());
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> findTeacherById(@PathVariable("id") Integer id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(), iTeacher.findOne(id)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("error", LocalTime.now().toString(), e.getMessage()));
        }
    }

    @GetMapping("/getByProfile/{id}")
    public ResponseEntity<?> findTeacherByProfileId(@PathVariable("id") Integer id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(iTeacher.findTeacherByProfileId(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("error", LocalTime.now().toString(), e.getMessage()));
        }
    }

    @GetMapping("/getByCard/{card}")
    public ResponseEntity<?> findTeacherByCard(@PathVariable("card") String card){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(iTeacher.findTeacherByCard(card));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("error", LocalTime.now().toString(), e.getMessage()));
        }
    }
}
