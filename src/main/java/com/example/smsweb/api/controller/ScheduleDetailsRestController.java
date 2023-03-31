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
import java.time.LocalDateTime;
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
    @PostMapping("/findScheduleDetailsOrNullByDate")
    public ResponseEntity<?> findScheduleDetailsOrNullByDate(@RequestParam("date") String date) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailsOrNullByDate(date)));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailsOrNullByShiftAndDateGreater")
    public ResponseEntity<?> findScheduleDetailsOrNullByShiftAndDateGreater(@RequestParam("date")String date,
                                                                            @RequestParam("shift")String shift){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailsByShiftAndDateAfter(shift, date)));
        }catch (Exception e){
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
    @PostMapping("/findSchedulesOrNullByDate")
    public ResponseEntity<?> findSchedulesOrNullByDate(@RequestParam("date") String date) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findSchedulesOrNullByDate(date)));
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
    public ResponseEntity<?> findScheduleDetailByDateBetween(@RequestParam("fromDate") String fromDate,
                                                             @RequestParam("toDate") String toDate) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailByDateBetween(fromDate, toDate)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailByDateBetweenAndScheduleId")
    public ResponseEntity<?> findScheduleDetailByDateBetweenAndScheduleId(@RequestParam("fromDate") String fromDate,
                                                                          @RequestParam("toDate") String toDate,
                                                                          @RequestParam("scheduleId") Integer scheduleId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailByDateBetweenAndScheduleId(fromDate, toDate, scheduleId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailsByDateBetweenAndScheduleIdAndTeacherId")
    public ResponseEntity<?> findScheduleDetailsByDateBetweenAndScheduleIdAndTeacherId(@RequestParam("fromDate") String fromDate,
                                                                                       @RequestParam("toDate") String toDate,
                                                                                       @RequestParam("scheduleId") Integer scheduleId,
                                                                                       @RequestParam("teacherId") Integer teacherId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailByDateBetweenAndScheduleIdAndTeacherId(fromDate, toDate, scheduleId, teacherId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailsByDateBetweenAndTeacherId")
    public ResponseEntity<?> findScheduleDetailsByDateBetweenAndTeacherId(@RequestParam("fromDate") String fromDate,
                                                                          @RequestParam("toDate") String toDate,
                                                                          @RequestParam("teacherId") Integer teacherId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDate.now().toString(), scheduleDetailsService.findScheduleDetailsByDateBetweenAndTeacherId(fromDate, toDate, teacherId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }


    @PostMapping("/findScheduleDetailsByDateAndTeacherId")
    public ResponseEntity<?> findScheduleDetailsByDateAndTeacherId(@RequestParam("date") String date,
                                                                   @RequestParam("teacherId") Integer teacherId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDateTime.now().toString(), scheduleDetailsService.findScheduleDetailsByDateAndTeacherId(date, teacherId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @PostMapping("/findScheduleDetailsByDateAndScheduleIdAndTeacherId")
    public ResponseEntity<?> findScheduleDetailsByDateAndScheduleIdAndTeacherId(@RequestParam("date") String date,
                                                                                @RequestParam("scheduleId") Integer scheduleId,
                                                                                @RequestParam("teacherId") Integer teacherId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDateTime.now().toString(), scheduleDetailsService.findScheduleDetailsByDateAndScheduleIdAndTeacherId(date, scheduleId, teacherId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }

    @GetMapping("/findScheduleByTeacher/{teacherId}")
    public ResponseEntity<?> findScheduleByTeacher(@PathVariable("teacherId")Integer teacherId){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success",LocalDate.now().toString(),scheduleDetailsService.findScheduleDetailByTeacher(teacherId)));
    }

    @PostMapping("/findScheduleDetailByDateSlotAndShift")
    public ResponseEntity<?> findScheduleDetailByDateSlotAndShift(@RequestParam("date")String date,
                                                                  @RequestParam("slot")Integer slot,
                                                                  @RequestParam("shift")String shift){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalDateTime.now().toString(), scheduleDetailsService.findScheduleDetailByDateSlotAndShift(date, slot, shift)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Don't find any records"));
        }
    }
}
