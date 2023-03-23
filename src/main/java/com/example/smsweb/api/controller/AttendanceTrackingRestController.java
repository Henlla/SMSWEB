package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IAttendanceTracking;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.AttendanceTracking;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/attendance_tracking")
@Slf4j
public class AttendanceTrackingRestController {
    @Autowired
    IAttendanceTracking service;
    List<AttendanceTracking> attendanceTrackingList;


    @PostMapping("/saveTracking")
    public ResponseEntity<?> save(@RequestParam("attendance_tracking") String attendance) {
        try {
            AttendanceTracking attendanceTracking = new ObjectMapper().readValue(attendance, AttendanceTracking.class);
            service.save(attendanceTracking);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Save success"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Post error"));
        }
    }

    @PostMapping("/findByDateAndTeacher")
    public ResponseEntity<?> findByDateAndTeacher(@RequestParam("fromDate") String fromDate,
                                                  @RequestParam("toDate") String toDate,
                                                  @RequestParam("teacherId") Integer teacherId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findByDateBetweenAndTeacherId(fromDate, toDate, teacherId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @GetMapping("/findByTeacherId/{id}")
    public ResponseEntity<?> findByTeacherId(@PathVariable("id") Integer teacherId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findByTeacherId(teacherId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Get fail"));
        }
    }
}
