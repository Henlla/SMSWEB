package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IStaff;
import com.example.smsweb.api.di.irepository.IStudent;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Staff;
import com.example.smsweb.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
@RestController
@RequestMapping("/api/staffs")
public class StaffRestController {
    @Autowired
    private IStaff iStaff;
    @PostMapping("/")
    public ResponseEntity<?> saveStudent(@ModelAttribute Staff staff){
        iStaff.save(staff);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(),staff));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> findStudentById(@PathVariable("id") Integer id){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(), iStaff.findOne(id)));
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAllStudent(){
        return ResponseEntity.status(HttpStatus.OK).body(iStaff.findAll());
    }
}
