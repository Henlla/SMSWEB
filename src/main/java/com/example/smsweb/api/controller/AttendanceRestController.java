package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IAttendance;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Attendance;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/attendance")
public class AttendanceRestController extends GenericController<Attendance> {
    @Autowired
    private IAttendance service;

    @PostMapping("/save")
    public ResponseEntity<?> post(@RequestParam("attendance") String attendanceJson) {
        try {
            Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
            service.save(attendance);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Tạo mới thành công", LocalDate.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Tạo mới thất bại", LocalDate.now().toString(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> put(@RequestParam("attendance") String attendanceJson) {
        try {
            Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
            service.save(attendance);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Cập nhật thành công", LocalDate.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Cập nhật thất bại", LocalDate.now().toString(), null));
        }
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestParam("list_attendance") String listAttendance) {
        try {
            List<Attendance> listAttend = new ObjectMapper().readValue(listAttendance, new TypeReference<List<Attendance>>() {
            });
            service.saveAllAttendance(listAttend);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Sao lưu thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), e.getMessage()));
        }
    }

    @PostMapping("/findAttendanceByDate")
    public ResponseEntity<?> findAttendanceByDate(@RequestParam("date") String date) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success",LocalDate.now().toString(),service.findAttendByDate(date)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error",LocalDate.now().toString(),e.getMessage()));
        }
    }
}
