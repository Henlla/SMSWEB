package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.ISchedule;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Schedule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/schedules/")
@Slf4j
public class ScheduleRestController {

    @Autowired
    ISchedule iSchedule;

    List<Schedule> listSchedule;

    @PostMapping("/post")
    public ResponseEntity<?> createSchedule(@RequestParam("schedule") String schedule) throws JsonProcessingException {
        log.debug("::::::START METHOD createSchedule ::::::");
        Schedule schedule1 = new ObjectMapper().readValue(schedule, Schedule.class);
        Schedule scheduleSave = iSchedule.saveSchedule(schedule1);
        log.debug("::::::FINISH METHOD createSchedule ::::::");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(), scheduleSave));
    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> getOneSchedule(@PathVariable("id") Integer id) {
        log.debug("::::::START METHOD getOneSchedule with id = {} ::::::", id);
        Schedule schedule = iSchedule.findOne(id);
        log.debug("::::::FINISH METHOD getOneSchedule ::::::");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(), schedule));
    }

    @PutMapping("/put")
    public ResponseEntity<?> putSchedule(@RequestParam("schedule") String scheduleJson) throws JsonProcessingException {
        log.debug("::::::START METHOD putSchedule ::::::");
        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
        iSchedule.saveSchedule(schedule);
        log.debug("::::::FINISH METHOD getOneSchedule ::::::");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(), schedule));
    }

    @PostMapping("/getScheduleByClassAndSemester")
    public ResponseEntity<?> getScheduleByClassAndSemester(@RequestParam("classId") Integer classId, @RequestParam("semester") Integer semester) throws JsonProcessingException {
        try {
            log.debug("::::::START METHOD getScheduleByClassAndSemester ::::::");
            Schedule schedule = iSchedule.findScheduleByClassAndSemester(classId, semester);
            log.debug("::::::FINISH METHOD getScheduleByClassAndSemester ::::::");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(), schedule));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDateTime.now().toString(), "e.getMessage()"));
        }
    }

    @GetMapping("/getScheduleByClassId/{id}")
    public ResponseEntity<?> getScheduleByClassId(@PathVariable("id") Integer classId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), iSchedule.findScheduleByClass(classId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/getScheduleByClass")
    public ResponseEntity<?> getScheduleByClass(@RequestParam("classId") Integer classId) throws JsonProcessingException {
        try {
            log.debug("::::::START METHOD getScheduleByClassAndSemester ::::::");
            List<Schedule> schedule = iSchedule.findScheduleByClassID(classId);
            log.debug("::::::FINISH METHOD getScheduleByClassAndSemester ::::::");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(), schedule));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDateTime.now().toString(), e.getMessage()));
        }
    }

    @GetMapping("/findScheduleById/{scheduleId}")
    public ResponseEntity<?> findScheduleById(@PathVariable("scheduleId") Integer scheduleId) {
        try {
            listSchedule = new ArrayList<>();
            listSchedule = iSchedule.findScheduleById(scheduleId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDateTime.now().toString(), listSchedule));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDateTime.now().toString(), "Don't find any records"));
        }
    }

    @GetMapping("/findOne/{scheduleId}")
    public ResponseEntity<?> findOne(@PathVariable("scheduleId") Integer scheduleId) {
        try {
            Schedule schedule = iSchedule.findOne(scheduleId);
            return ResponseEntity.status(HttpStatus.OK).body(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDateTime.now().toString(), "Don't find any records"));
        }
    }
}
