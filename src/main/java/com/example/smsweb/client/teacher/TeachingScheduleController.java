package com.example.smsweb.client.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.WeekOfYear;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Profile;
import com.example.smsweb.models.Schedule;
import com.example.smsweb.models.ScheduleDetail;
import com.example.smsweb.models.Teacher;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

import java.time.DayOfWeek;

@Controller
@RequestMapping("/teacher/")
public class TeachingScheduleController {
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";

    @GetMapping("teaching_schedule")
    public String getMethodName(@CookieValue(name = "_token", defaultValue = "") String _token, Model model,
            Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                HttpHeaders headers = new HttpHeaders();
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();
                headers.set("Authorization", "Bearer " + _token);
                Account teacherUser = (Account) auth.getPrincipal();
                // Lấy Profile
                HttpEntity<String> requestProfile = new HttpEntity<>(headers);
                ResponseEntity<Profile> profileResponse = restTemplate.exchange(
                        PROFILE_URL + "get/" + teacherUser.getId(), HttpMethod.GET, requestProfile, Profile.class);
                // Lấy teacher theo profile id
                HttpEntity<String> requestTeacher = new HttpEntity<>(headers);
                ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(
                        TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET,
                        requestTeacher, Teacher.class);
                HttpEntity<Object> requestLisClass = new HttpEntity<Object>(headers);
                ResponseEntity<ResponseModel> listClassResponse = restTemplate.exchange(
                        CLASS_URL + "findClassByTeacher/" + teacherResponse.getBody().getId(), HttpMethod.GET,
                        requestLisClass, ResponseModel.class);
                String json = objectMapper.writeValueAsString(listClassResponse.getBody().getData());
                List<Classses> listClass = objectMapper.readValue(json, new TypeReference<List<Classses>>() {
                });
                model.addAttribute("classList", listClass);
                return "teacherDashboard/teaching_schedule/teaching_schedule";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    @GetMapping("view_teaching_schedule/{classCode}")
    public String view_teaching_schedule(@CookieValue(name = "_token", defaultValue = "") String _token,
            @PathVariable("classCode") String classCode,
            Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                HttpHeaders headers = new HttpHeaders();
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("classCode", classCode);
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode",
                        HttpMethod.POST, request, ResponseModel.class);
                String json = objectMapper.writeValueAsString(response.getBody().getData());
                Classses classses = objectMapper.readValue(json, Classses.class);

                List<Schedule> listSchedule = classses.getSchedulesById();
                LocalDate now = LocalDate.now();
                Integer week = now.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                List<ScheduleDetail> newList = new ArrayList<>();
                for (Schedule schedule : listSchedule) {
                    for (ScheduleDetail scheduleDetails : schedule.getScheduleDetailsById()) {
                        if (LocalDate.parse(scheduleDetails.getDate())
                                .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == week) {
                            newList.add(scheduleDetails);
                        }
                    }
                }
                newList = newList.stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).toList();

                LocalDate firstDay = now.with(firstDayOfYear()); // 2015-01-01
                LocalDate lastDay = now.with(lastDayOfYear()); // 2015-12-31
                int weekOfFirstDay = firstDay.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                int weekOfLastDay = lastDay.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM");
                List<WeekOfYear> weekOfYearList = new ArrayList<>();
                for (int i = weekOfFirstDay; i <= weekOfLastDay; i++) {
                    StringBuilder toDate = new StringBuilder();
                    for (LocalDate date = firstDay; date.isBefore(lastDay.plusDays(1)); date = date.plusDays(1)) {
                        int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                        if (i == weekOfYear) {
                            DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                            LocalDate startOfCurrentWeek = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                            DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(6);
                            LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
                            if (date.equals(startOfCurrentWeek)) {
                                toDate.append(date.format(dateTimeFormatter)).append(" To ");
                            } else if (date.equals(endOfWeek)) {
                                toDate.append(date.format(dateTimeFormatter));
                                WeekOfYear w = new WeekOfYear();
                                w.setDate(toDate.toString());
                                w.setWeek(weekOfYear);
                                weekOfYearList.add(w);
                            }
                        } else {
                            firstDay = date;
                            break;
                        }
                    }
                }
                model.addAttribute("listWeek", weekOfYearList);
                model.addAttribute("scheduleList", new ObjectMapper().writeValueAsString(newList));
                model.addAttribute("currenWeek", week);
                model.addAttribute("classses", classses);
                return "teacherDashboard/teaching_schedule/view_teaching_schedule";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("viewTeachingScheduleByWeek")
    @ResponseBody
    public Object viewTeachingScheduleByWeek(@CookieValue(name = "_token", defaultValue = "") String _token,
            @RequestParam("classCode") String classCode,
            @RequestParam("week") int week,
            Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                HttpHeaders headers = new HttpHeaders();
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("classCode", classCode);
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode",
                        HttpMethod.POST, request, ResponseModel.class);
                String json = objectMapper.writeValueAsString(response.getBody().getData());
                Classses classses = objectMapper.readValue(json, Classses.class);

                List<Schedule> listSchedule = classses.getSchedulesById();
                List<ScheduleDetail> newList = new ArrayList<>();
                for (Schedule schedule : listSchedule) {
                    for (ScheduleDetail scheduleDetails : schedule.getScheduleDetailsById()) {
                        if (LocalDate.parse(scheduleDetails.getDate())
                                .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == week) {
                            newList.add(scheduleDetails);
                        }
                    }
                }
                if(newList.isEmpty()){
                    return "error";
                }else{
                    newList = newList.stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).toList();
                    return newList;
                }
            } else {
                return "token expired";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
