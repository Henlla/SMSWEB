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
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), "Save success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDate.now().toString(), "Save fail"));
        }

    }

    @PutMapping("put")
    public ResponseEntity<?> putScheduleDetails(@RequestParam("schedule_details") String schedule_details) {
        try {
            ScheduleDetail scheduleDetail = new ObjectMapper().readValue(schedule_details, ScheduleDetail.class);
            scheduleDetailsService.putScheduleDetails(scheduleDetail);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), "Update success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDate.now().toString(), "Update fail"));
        }

    }

    @PostMapping("findScheduleDetail")
    public ResponseEntity<?> findScheduleDetail(@RequestParam("date") String date,
                                                @RequestParam("scheduleId") String scheduleId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetail(date, scheduleId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("findScheduleDetailBySlot")
    public ResponseEntity<?> findScheduleDetail(@RequestParam("date") String date,
                                                @RequestParam("scheduleId") String scheduleId,
                                                @RequestParam("slot") String slot) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailBySlot(date, scheduleId, slot)));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> findOneScheduleDetails(@PathVariable("id") Integer id) {
        try {
            ScheduleDetail scheduleDetail = scheduleDetailsService.findScheduleDetailById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), scheduleDetail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDate.now().toString(), "Don't find any records"));
        }

    }

    @PostMapping("/findScheduleByDate")
    public ResponseEntity<?> findScheduleByDate(@RequestParam("date") String date) {
        try {
            ScheduleDetail scheduleDetail = scheduleDetailsService.findScheduleDetailsByDate(date);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), scheduleDetail));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailsByDate")
    public ResponseEntity<?> findScheduleDetailsByDate(@RequestParam("date") String date) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleByDate(date)));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailByScheduleIdAndSubjectId")
    public ResponseEntity<?> findScheduleDetailByScheduleIdAndSubjectId(@RequestParam("scheduleId") Integer scheduleId,
                                                                        @RequestParam("subjectId") Integer subjectId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailByScheduleIdAndSubjectId(scheduleId, subjectId)));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailByDateBetween")
    public ResponseEntity<?> findScheduleDetailByDateBetween(@RequestParam("fromDate") String fromDate,@RequestParam("toDate")String toDate){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success",LocalDate.now().toString(),scheduleDetailsService.findScheduleDetailByDateBetween(fromDate, toDate)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error",LocalDate.now().toString(),"Don't find any records"));
        }
    }
    @PostMapping("/findScheduleDetailByDateBetweenAndScheduleId")
    public ResponseEntity<?> findScheduleDetailByDateBetweenAndScheduleId(@RequestParam("fromDate") String fromDate,
                                                                          @RequestParam("toDate")String toDate,
                                                                          @RequestParam("scheduleId")Integer scheduleId){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success",LocalDate.now().toString(),scheduleDetailsService.findScheduleDetailByDateBetweenAndScheduleId(fromDate, toDate,scheduleId)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error",LocalDate.now().toString(),"Don't find any records"));
        }
    }
}
