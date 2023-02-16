package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IAttendance;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Attendance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/attendance")
public class AttendanceRestController extends GenericController<Attendance> {
    @Autowired
    private IAttendance service;

    @PostMapping("/save")
    public ResponseEntity<?> post(@RequestParam("attendance") String attendanceJson) {
        try {
            Attendance attendance = new ObjectMapper().readValue(attendanceJson,Attendance.class);
            service.save(attendance);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Tạo mới thành công", LocalDate.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Tạo mới thất bại", LocalDate.now().toString(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> put(@RequestParam("attendance") String attendanceJson){
        try {
            Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
           service.save(attendance);
           return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Cập nhật thành công",LocalDate.now().toString(),null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Cập nhật thất bại",LocalDate.now().toString(),null));
        }
    }
}
