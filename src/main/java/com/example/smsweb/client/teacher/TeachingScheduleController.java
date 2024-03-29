package com.example.smsweb.client.teacher;

import com.example.smsweb.dto.TeachingCurrenDate;
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

    @GetMapping("view_teaching_schedule")
    public String view_teaching_schedule(@CookieValue(name = "_token", defaultValue = "") String _token,Authentication auth,
            Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                HttpHeaders headers = new HttpHeaders();
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();
                headers.set("Authorization", "Bearer " + _token);
                Account teacherUser = (Account) auth.getPrincipal();
                // Lấy Profile
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<Profile> profileResponse = restTemplate.exchange(
                        PROFILE_URL + "get/" + teacherUser.getId(), HttpMethod.GET, request, Profile.class);
                // Lấy teacher theo profile id
                ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(
                        TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET,
                        request, Teacher.class);

                ResponseEntity<ResponseModel> responseScheduleDetails = restTemplate.exchange(SCHEDULE_DETAIL_URL+"findScheduleByTeacher/"+teacherResponse.getBody().getId(),HttpMethod.GET,request,ResponseModel.class);

                String jsonScheduleDetails = objectMapper.writeValueAsString(responseScheduleDetails.getBody().getData());
                List<ScheduleDetail> scheduleDetails = objectMapper.readValue(jsonScheduleDetails, new TypeReference<List<ScheduleDetail>>() {
                });

                LocalDate now = LocalDate.now();
                Integer week = now.get(WeekFields.of(Locale.getDefault()).weekOfYear());
                Integer year = now.getYear();
                List<TeachingCurrenDate> teachingCurrenDateList = new ArrayList<>();
                for (ScheduleDetail scheduleDetail:scheduleDetails){
                    if (LocalDate.parse(scheduleDetail.getDate())
                            .get(WeekFields.of(Locale.getDefault()).weekOfYear()) == week && LocalDate.parse(scheduleDetail.getDate()).getYear() == year) {
                        ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL+"get/"+scheduleDetail.getScheduleId(),HttpMethod.POST,request,ResponseModel.class);
                        String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                        Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                        ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL+"getClass/"+schedule.getClassId(),HttpMethod.GET,request,ResponseModel.class);
                        String jsonClass = objectMapper.writeValueAsString(responseClass.getBody().getData());
                        Classses classses = objectMapper.readValue(jsonClass,Classses.class);
                        TeachingCurrenDate teachingCurrenDate = new TeachingCurrenDate();
                        teachingCurrenDate.setDate(scheduleDetail.getDate());
                        teachingCurrenDate.setClassCode(classses.getClassCode());
                        teachingCurrenDate.setShift(classses.getShift());
                        teachingCurrenDate.setDepartmentCode(classses.getDepartmentByDepartmentId().getDepartmentCode());
                        teachingCurrenDate.setSlot(scheduleDetail.getSlot());
                        teachingCurrenDate.setSubject(scheduleDetail.getSubjectBySubjectId());
                        teachingCurrenDate.setDayOfWeek(scheduleDetail.getDayOfWeek());
                        teachingCurrenDate.setRoomCode(classses.getClassRoom().getRoomCode());
                        teachingCurrenDateList.add(teachingCurrenDate);
                    }
                }

                teachingCurrenDateList = teachingCurrenDateList.stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).toList();

                LocalDate firstDay = now.with(firstDayOfYear()); // 2015-01-01
                LocalDate lastDay = now.with(lastDayOfYear()); // 2015-12-31
                int weekOfFirstDay = firstDay.get(WeekFields.of(Locale.getDefault()).weekOfYear());
                int weekOfLastDay = lastDay.get(WeekFields.of(Locale.getDefault()).weekOfYear());
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM");
                List<WeekOfYear> weekOfYearList = new ArrayList<>();
                for (int i = weekOfFirstDay; i <= weekOfLastDay; i++) {
                    StringBuilder toDate = new StringBuilder();
                    for (LocalDate date = firstDay; date.isBefore(lastDay.plusDays(1)); date = date.plusDays(1)) {
                        int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
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
                model.addAttribute("scheduleList", new ObjectMapper().writeValueAsString(teachingCurrenDateList));
                model.addAttribute("currenWeek", week);
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
            Authentication auth,
            @RequestParam("week") int week,
            Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                HttpHeaders headers = new HttpHeaders();
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();
                headers.set("Authorization", "Bearer " + _token);
                Account teacherUser = (Account) auth.getPrincipal();
                // Lấy Profile
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<Profile> profileResponse = restTemplate.exchange(
                        PROFILE_URL + "get/" + teacherUser.getId(), HttpMethod.GET, request, Profile.class);
                // Lấy teacher theo profile id
                ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(
                        TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET,
                        request, Teacher.class);

                ResponseEntity<ResponseModel> responseScheduleDetails = restTemplate.exchange(SCHEDULE_DETAIL_URL+"findScheduleByTeacher/"+teacherResponse.getBody().getId(),HttpMethod.GET,request,ResponseModel.class);

                String jsonScheduleDetails = objectMapper.writeValueAsString(responseScheduleDetails.getBody().getData());
                List<ScheduleDetail> scheduleDetails = objectMapper.readValue(jsonScheduleDetails, new TypeReference<List<ScheduleDetail>>() {
                });
                LocalDate now = LocalDate.now();
                List<TeachingCurrenDate> teachingCurrenDateList = new ArrayList<>();
                Integer year = now.getYear();
                for (ScheduleDetail scheduleDetail:scheduleDetails){
                    if (LocalDate.parse(scheduleDetail.getDate())
                            .get(WeekFields.of(Locale.getDefault()).weekOfYear()) == week && LocalDate.parse(scheduleDetail.getDate()).getYear()==year) {
                        ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL+"get/"+scheduleDetail.getScheduleId(),HttpMethod.POST,request,ResponseModel.class);
                        String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                        Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                        ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL+"getClass/"+schedule.getClassId(),HttpMethod.GET,request,ResponseModel.class);
                        String jsonClass = objectMapper.writeValueAsString(responseClass.getBody().getData());
                        Classses classses = objectMapper.readValue(jsonClass,Classses.class);
                        TeachingCurrenDate teachingCurrenDate = new TeachingCurrenDate();
                        teachingCurrenDate.setDate(scheduleDetail.getDate());
                        teachingCurrenDate.setClassCode(classses.getClassCode());
                        teachingCurrenDate.setShift(classses.getShift());
                        teachingCurrenDate.setDepartmentCode(classses.getDepartmentByDepartmentId().getDepartmentCode());
                        teachingCurrenDate.setSlot(scheduleDetail.getSlot());
                        teachingCurrenDate.setSubject(scheduleDetail.getSubjectBySubjectId());
                        teachingCurrenDate.setDayOfWeek(scheduleDetail.getDayOfWeek());
                        teachingCurrenDate.setRoomCode(classses.getClassRoom().getRoomCode());
                        teachingCurrenDateList.add(teachingCurrenDate);
                    }
                }
                if(teachingCurrenDateList.isEmpty()){
                    return "error";
                }else{
                    teachingCurrenDateList = teachingCurrenDateList.stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).toList();
                    return teachingCurrenDateList;
                }
            } else {
                return "token expired";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
