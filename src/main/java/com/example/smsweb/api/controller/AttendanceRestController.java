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
    List<Attendance> listAttendance;

    @PostMapping("/save")
    public ResponseEntity<?> post(@RequestParam("attendance") String attendanceJson) {
        try {
            Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
            service.save(attendance);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Save success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Save fail"));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> put(@RequestParam("attendance") String attendanceJson) {
        try {
            Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
            service.save(attendance);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Update success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Update fail"));
        }
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestParam("list_attendance") String listAttendance) {
        try {
            List<Attendance> listAttend = new ObjectMapper().readValue(listAttendance, new TypeReference<List<Attendance>>() {
            });
            service.saveAllAttendance(listAttend);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), "Update success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Update fail"));
        }
    }

    @PostMapping("/findAttendanceByDate")
    public ResponseEntity<?> findAttendanceByDate(@RequestParam("date") String date) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findAttendByDate(date)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findAttendanceByDateAndSlot")
    public ResponseEntity<?> findAttendanceByDateAndSlot(@RequestParam("date") String date, @RequestParam("slot") Integer slot) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findAttendanceByDateAndSlot(date, slot)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findAttendanceByDateSlotStudentSubject")
    public ResponseEntity<?> findAttendanceByDateSlotStudentSubject(
            @RequestParam("date") String date
            , @RequestParam("slot") String slot
            , @RequestParam("student_subject") String student_subject) {
        try {
            Attendance attendance = new Attendance();
            attendance = service.findAttendanceByDateSlotStudentSubject(date, slot, student_subject);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), attendance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findAttendanceByStudentSubjectId")
    public ResponseEntity<?> findAttendanceByStudentSubjectId(@RequestParam("student_subject_id") Integer studentSubjectId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findAttendanceByStudentSubjectId(studentSubjectId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findAttendanceByDateAndSlotAndStudentSubject")
    public ResponseEntity<?> findAttendanceByDateAndStudentSubject(@RequestParam("date") String date,
                                                                   @RequestParam("slot") Integer slot,
                                                                   @RequestParam("studentSubjectId") Integer studentSubjectId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findAttendancesByDateAndSlotAndStudentSubjectId(date, slot, studentSubjectId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findAttendancesByDateAndSlotAndStudentSubjectAndShift")
    public ResponseEntity<?> findAttendancesByDateAndStudentSubjectAndShift(@RequestParam("date") String date,
                                                                            @RequestParam("slot") Integer slot,
                                                                            @RequestParam("studentSubjectId") Integer studentSubjectId,
                                                                            @RequestParam("shift") String shift) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findAttendancesByDateAndSlotAndStudentSubjectIdAndShift(date, slot, studentSubjectId, shift)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findAttendanceByDateAndSlotAndStudentSubjectAndShift")
    public ResponseEntity<?> findAttendanceByDateAndStudentSubjectAndShift(@RequestParam("date") String date,
                                                                           @RequestParam("slot") Integer slot,
                                                                           @RequestParam("studentSubjectId") Integer studentSubjectId,
                                                                           @RequestParam("shift") String shift) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), service.findAttendanceByDateAndSlotAndStudentSubjectIdAndShift(date, slot, studentSubjectId, shift)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

}
