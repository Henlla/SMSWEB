package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Schedule;
import com.example.smsweb.models.ScheduleDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.smsweb.utils.StreamHelper.distinctByKey;

@Controller
@RequestMapping("dashboard/attendance")
@Slf4j
public class AttendanceAdminController {

    private final String URL_SCHEDULE_DETAIL = "http://localhost:8080/api/schedules_detail/";
    private final String URL_SCHEDULE = "http://localhost:8080/api/schedules/";
    private final String URL_CLASS = "http://localhost:8080/api/classes/";

    RestTemplate restTemplate;

    List<ScheduleDetail> listScheduleDetail;
    List<Schedule> listSchedule;
    List<Classses> listClass;

    @GetMapping("/index")
    public Object index(@CookieValue("_token") String _token, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                return "dashboard/attendance/attendance";
            } else {
                return "redirect:/dashboard/login";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/dashboard/login";
        }
    }

    @GetMapping("/findScheduleDetailByDate")
    @ResponseBody
    public Object findScheduleDetailByDate(@CookieValue(name = "_token") String _token, @RequestParam("date") String date) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listScheduleDetail = new ArrayList<>();
                listSchedule = new ArrayList<>();
                listClass = new ArrayList<>();

                int dd = Integer.parseInt(date.split("/")[0]);
                int mm = Integer.parseInt(date.split("/")[1]);
                int yy = Integer.parseInt(date.split("/")[2]);
                String currentDay = LocalDate.parse(LocalDate.of(yy, mm, dd).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).toString();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy schedule detail
                MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                scheduleDetailContent.add("date", currentDay);
                HttpEntity<MultiValueMap<String, String>> scheduleDetailRequest = new HttpEntity<>(scheduleDetailContent, headers);
                ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(URL_SCHEDULE_DETAIL + "findScheduleDetailsByDate", HttpMethod.POST, scheduleDetailRequest, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                listScheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, new TypeReference<List<ScheduleDetail>>() {
                });

                // Lấy schedule
                for (ScheduleDetail scheduleDetail : listScheduleDetail) {
                    ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(URL_SCHEDULE + "get/" + scheduleDetail.getScheduleId(), HttpMethod.POST, request, ResponseModel.class);
                    String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                    Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                    listSchedule.add(schedule);
                }

                // Lấy class
                for (Schedule schedule : listSchedule) {
                    ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(URL_CLASS + "getClass/" + schedule.getClassId(), HttpMethod.GET, request, ResponseModel.class);
                    String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                    Classses classses = new ObjectMapper().readValue(classJson, Classses.class);
                    listClass.add(classses);
                }

                List<Classses> filterClass = listClass.stream().filter(distinctByKey(Classses::getId)).toList();
                return filterClass;
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Something wrong", HttpStatus.BAD_REQUEST);
        }
    }
}
