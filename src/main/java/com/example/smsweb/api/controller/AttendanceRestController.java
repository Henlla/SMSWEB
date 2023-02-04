package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IAttendance;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("api/attendance")
public class AttendanceRestController extends GenericController<Attendance> {
    @Autowired
    private IAttendance service;

    @PostMapping("/post")
    public ResponseEntity<?> post(@ModelAttribute Attendance attendance) {
        try {
            service.save(attendance);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Tạo mới thành công", LocalTime.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Tạo mới thất bại", LocalTime.now().toString(), null));
        }
    }

    @PutMapping("/put")
    public ResponseEntity<?> put(@ModelAttribute Attendance attendance){
        try {
           service.save(attendance);
           return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Cập nhật thành công",LocalTime.now().toString(),null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Cập nhật thất bại",LocalTime.now().toString(),null));
        }
    }
}
