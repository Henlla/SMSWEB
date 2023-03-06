package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.ScheduleDetailsService;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.ScheduleDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/schedules_detail/")
@Slf4j
public class ScheduleDetailsRestController {
    @Autowired
    private ScheduleDetailsService scheduleDetailsService;

    @PostMapping("saveAll")
    public ResponseEntity<?> saveAll(@RequestParam("listSchedule") String listSchedule) {
        try {
            List<ScheduleDetail> list = new ObjectMapper().readValue(listSchedule, new TypeReference<List<ScheduleDetail>>() {
            });
            scheduleDetailsService.addScheduleList(list);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), "ok"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDate.now().toString(), e.getMessage()));
        }

    }

    @PutMapping("put")
    public ResponseEntity<?> putScheduleDetails(@RequestParam("schedule_details") String schedule_details) {
        try {
            ScheduleDetail scheduleDetail = new ObjectMapper().readValue(schedule_details, ScheduleDetail.class);
            scheduleDetailsService.putScheduleDetails(scheduleDetail);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), "Cập nhật thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDate.now().toString(), e.getMessage()));
        }

    }

    @PostMapping("findScheduleByDate")
    public ResponseEntity<?> findScheduleByDate(@RequestParam("date") String date) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleByDate(date)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), e.getMessage()));
        }
    }

    @PostMapping("findScheduleDetail")
    public ResponseEntity<?> findScheduleDetail(@RequestParam("date") String date, @RequestParam("scheduleId") String scheduleId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetail(date, scheduleId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), e.getMessage()));
        }
    }
}
