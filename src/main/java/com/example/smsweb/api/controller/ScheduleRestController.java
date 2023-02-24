package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.ISchedule;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Schedule;
import com.google.api.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/schedules/")
@Slf4j
public class ScheduleRestController {

    @Autowired
    ISchedule iSchedule;

    @PostMapping("/post")
    public ResponseEntity<?> createSchedule(@ModelAttribute Schedule schedule) {
        log.debug("::::::START METHOD createSchedule ::::::");
        iSchedule.save(schedule);
        log.debug("::::::FINISH METHOD createSchedule ::::::");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(), schedule));
    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> getOneSchedule(@PathVariable("id") Integer id) {
        log.debug("::::::START METHOD getOneSchedule with id = {} ::::::",id);
        Schedule schedule = iSchedule.findOne(id);
        log.debug("::::::FINISH METHOD getOneSchedule ::::::");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(), schedule));
    }
}
