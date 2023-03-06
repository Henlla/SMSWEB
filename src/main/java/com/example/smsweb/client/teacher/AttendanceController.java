package com.example.smsweb.client.teacher;

import com.example.smsweb.dto.AttendanceEdit;
import com.example.smsweb.dto.AttendanceModel;
import com.example.smsweb.dto.AttendanceView;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.FormatDate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("teacher/attendance")
public class AttendanceController {
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String STUDENT_SUBJECT_URL = "http://localhost:8080/api/student-subject/";
    private final String ATTENDANCE_URL = "http://localhost:8080/api/attendance/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    RestTemplate restTemplate;
    List<Schedule> listSchedule;
    List<ScheduleDetail> listScheduleDetail;
    List<Classses> listClass;
    List<Attendance> listAttendance;
    List<AttendanceView> listAttendanceView;
    List<StudentSubject> listStudentSubject;
    List<Student> listStudent;
    AttendanceView attendanceView;
    List<AttendanceEdit> listEditAttendance;

    @GetMapping("/index")
    public String attendance(@CookieValue(name = "_token", defaultValue = "") String _token, Model model, Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                listSchedule = new ArrayList<>();
                listScheduleDetail = new ArrayList<>();
                listClass = new ArrayList<>();
                listAttendanceView = new ArrayList<>();
                restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, String> content = new LinkedMultiValueMap<String, String>();
                String date = FormatDate.dateFormat(LocalDate.now(), "yyyy-mm-dd");
                content.add("date", date);
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(content, headers);

                //Lấy Schedule Detail Của ngày hôm nay
                ResponseEntity<ResponseModel> scheduleDetailResponse = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleByDate", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(scheduleDetailResponse.getBody().getData());
                listScheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, new TypeReference<List<ScheduleDetail>>() {
                });
                if (listScheduleDetail.size() != 0) {
                    for (ScheduleDetail scheduleDetail : listScheduleDetail) {
                        //Lấy schedule
                        HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                        ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "get/" + scheduleDetail.getScheduleId(), HttpMethod.POST, requestSchedule, ResponseModel.class);
                        String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                        listSchedule.add(schedule);
                    }
                    for (Schedule schedule : listSchedule) {
                        // Lấy User hiên tại
                        Account teacherUser = (Account) auth.getPrincipal();
                        // Lấy Profile
                        HttpEntity<String> requestProfile = new HttpEntity<>(headers);
                        ResponseEntity<Profile> profileResponse = restTemplate.exchange(PROFILE_URL + "get/" + teacherUser.getId(), HttpMethod.GET, requestProfile, Profile.class);
                        // Lấy teacher theo profile id
                        HttpEntity<String> requestTeacher = new HttpEntity<>(headers);
                        ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, requestTeacher, Teacher.class);
                        // Lấy class
                        MultiValueMap<String, String> classContent = new LinkedMultiValueMap<>();
                        classContent.add("teacherId", String.valueOf(teacherResponse.getBody().getId()));
                        classContent.add("scheduleId", String.valueOf(schedule.getClassId()));
                        HttpEntity<MultiValueMap<String, String>> requestClass = new HttpEntity<>(classContent, headers);
                        ResponseEntity<ResponseModel> classResponse = restTemplate.exchange(CLASS_URL + "findClassByTeacherAndSchedule", HttpMethod.POST, requestClass, ResponseModel.class);
                        String classJon = new ObjectMapper().writeValueAsString(classResponse.getBody().getData());
                        Classses classses = new ObjectMapper().readValue(classJon, Classses.class);
                        listClass.add(classses);
                    }
                    for (Classses classes : listClass) {
                        attendanceView = new AttendanceView();
                        // Lấy schedule theo class
                        HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                        ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass/" + classes.getId(), HttpMethod.GET, requestSchedule, ResponseModel.class);
                        String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                        String dateSchedule = FormatDate.dateFormat(LocalDate.now(), "yyyy-mm-dd");
                        // Lấy schedule detail theo schedule
                        MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                        scheduleDetailContent.add("date", dateSchedule);
                        scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                        HttpEntity<MultiValueMap<String, String>> scheduleDetailRequest = new HttpEntity<>(scheduleDetailContent, headers);
                        ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetail", HttpMethod.POST, scheduleDetailRequest, ResponseModel.class);
                        String jsonScheduleDetail = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                        ScheduleDetail scheduleDetail = new ObjectMapper().readValue(jsonScheduleDetail, ScheduleDetail.class);
                        attendanceView.setClass_name(classes.getClassCode());
                        attendanceView.setSubject_name(scheduleDetail.getSubjectBySubjectId().getSubjectName());
                        String[] splitDate = scheduleDetail.getDate().split("-");
                        attendanceView.setDate(splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0]);
                        attendanceView.setClass_id(classes.getId());
                        attendanceView.setSubject_code(scheduleDetail.getSubjectBySubjectId().getSubjectCode());
                        listAttendanceView.add(attendanceView);
                    }
                    model.addAttribute("listClass", listAttendanceView);
                } else {
                    model.addAttribute("listClass", listAttendanceView);
                }
                return "teacher/attendance";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/listStudentInClass/{id}")
    @ResponseBody
    public Object listStudentInClass(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") String classId) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(STUDENT_CLASS_URL + "/getStudentByClassCode/" + classId, HttpMethod.GET, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>("Không tìm thấy dữ liệu", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/submitAttendance")
    @ResponseBody
    public Object submitAttendance(@CookieValue(name = "_token", defaultValue = "") String _token,
                                   @RequestParam("attendModel") String attenModel,
                                   @RequestParam("classId") String classId,
                                   @RequestParam("date") String date) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listAttendance = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                // Lấy schedule
                HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass/" + classId, HttpMethod.GET, requestSchedule, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);

                //Lấy schedule detail
                MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                scheduleDetailContent.add("date", date);
                scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetail", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                ScheduleDetail scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);

                //Lấy attendance model
                List<AttendanceModel> attendanceModel = new ObjectMapper().readValue(attenModel, new TypeReference<List<AttendanceModel>>() {
                });
                for (AttendanceModel attend : attendanceModel) {
                    MultiValueMap<String, String> attenModelContent = new LinkedMultiValueMap<>();
                    attenModelContent.add("studentId", attend.getStudent_id());
                    attenModelContent.add("subjectId", String.valueOf(scheduleDetail.getSubjectId()));
                    HttpEntity<MultiValueMap<String, String>> requestStudentSubject = new HttpEntity<>(attenModelContent, headers);
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, requestStudentSubject, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                    Attendance attendance = new Attendance();
                    attendance.setStudentSubjectId(studentSubject.getId());
                    attendance.setNote(attend.getNote());
                    attendance.setStatus(attend.getStatus().equals("Present") ? 1 : 0);
                    attendance.setDate(date);
                    listAttendance.add(attendance);
                }
                MultiValueMap<String, String> contentAttendance = new LinkedMultiValueMap<>();
                String attendanceJson = new ObjectMapper().writeValueAsString(listAttendance);
                contentAttendance.add("list_attendance", attendanceJson);
                HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(contentAttendance, headers);
                ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "saveAll", HttpMethod.POST, requestAttendance, ResponseModel.class);
                return responseAttendance.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/edit")
    public Object editAttendance(@CookieValue(name = "_token", defaultValue = "") String _token, Model model, Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                attendanceView = new AttendanceView();
                listSchedule = new ArrayList<>();
                listScheduleDetail = new ArrayList<>();
                listClass = new ArrayList<>();
                listAttendanceView = new ArrayList<>();
                restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, String> content = new LinkedMultiValueMap<String, String>();
                String date = FormatDate.dateFormat(LocalDate.now(), "yyyy-mm-dd");
                content.add("date", date);
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(content, headers);

                //Lấy Schedule Detail Của ngày hôm nay
                ResponseEntity<ResponseModel> scheduleDetailResponse = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleByDate", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(scheduleDetailResponse.getBody().getData());
                listScheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, new TypeReference<List<ScheduleDetail>>() {
                });
                if (listScheduleDetail.size() != 0) {
                    for (ScheduleDetail scheduleDetail : listScheduleDetail) {
                        //Lấy schedule
                        HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                        ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "get/" + scheduleDetail.getScheduleId(), HttpMethod.POST, requestSchedule, ResponseModel.class);
                        String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                        listSchedule.add(schedule);
                    }
                    for (Schedule schedule : listSchedule) {
                        // Lấy User hiên tại
                        Account teacherUser = (Account) auth.getPrincipal();
                        // Lấy Profile
                        HttpEntity<String> requestProfile = new HttpEntity<>(headers);
                        ResponseEntity<Profile> profileResponse = restTemplate.exchange(PROFILE_URL + "get/" + teacherUser.getId(), HttpMethod.GET, requestProfile, Profile.class);
                        // Lấy teacher theo profile id
                        HttpEntity<String> requestTeacher = new HttpEntity<>(headers);
                        ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, requestTeacher, Teacher.class);
                        // Lấy class
                        MultiValueMap<String, String> classContent = new LinkedMultiValueMap<>();
                        classContent.add("teacherId", String.valueOf(teacherResponse.getBody().getId()));
                        classContent.add("scheduleId", String.valueOf(schedule.getClassId()));
                        HttpEntity<MultiValueMap<String, String>> requestClass = new HttpEntity<>(classContent, headers);
                        ResponseEntity<ResponseModel> classResponse = restTemplate.exchange(CLASS_URL + "findClassByTeacherAndSchedule", HttpMethod.POST, requestClass, ResponseModel.class);
                        String classJon = new ObjectMapper().writeValueAsString(classResponse.getBody().getData());
                        Classses classses = new ObjectMapper().readValue(classJon, Classses.class);
                        listClass.add(classses);
                    }
                    for (Classses classes : listClass) {
                        // Lấy schedule theo class
                        HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                        ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass/" + classes.getId(), HttpMethod.GET, requestSchedule, ResponseModel.class);
                        String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                        String dateSchedule = FormatDate.dateFormat(LocalDate.now(), "yyyy-mm-dd");
                        // Lấy schedule detail theo schedule
                        MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                        scheduleDetailContent.add("date", dateSchedule);
                        scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                        HttpEntity<MultiValueMap<String, String>> scheduleDetailRequest = new HttpEntity<>(scheduleDetailContent, headers);
                        ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetail", HttpMethod.POST, scheduleDetailRequest, ResponseModel.class);
                        String jsonScheduleDetail = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                        ScheduleDetail scheduleDetail = new ObjectMapper().readValue(jsonScheduleDetail, ScheduleDetail.class);
                        attendanceView.setClass_name(classes.getClassCode());
                        attendanceView.setSubject_name(scheduleDetail.getSubjectBySubjectId().getSubjectName());
                        String[] splitDate = scheduleDetail.getDate().split("-");
                        attendanceView.setDate(splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0]);
                        attendanceView.setClass_id(classes.getId());
                        attendanceView.setSubject_code(scheduleDetail.getSubjectBySubjectId().getSubjectCode());
                        listAttendanceView.add(attendanceView);
                    }
                    model.addAttribute("listClass", listAttendanceView);
                } else {
                    model.addAttribute("listClass", listAttendanceView);
                }
                return "teacher/edit";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/findAttendanceByDate")
    @ResponseBody
    public Object findAttendanceByDate(@CookieValue(name = "_token") String _token, @RequestParam("date") String date, @RequestParam("classId") String classId) {
        try {
            restTemplate = new RestTemplate();
            listStudentSubject = new ArrayList<>();
            listStudent = new ArrayList<>();
            listEditAttendance = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, String> contentAttendance = new LinkedMultiValueMap<>();
            contentAttendance.add("date", date);
            HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(contentAttendance, headers);
            ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDate", HttpMethod.POST, requestAttendance, ResponseModel.class);
            String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
            List<Attendance> listAttendance = new ObjectMapper().readValue(attendanceJson, new TypeReference<List<Attendance>>() {
            });
            if (listAttendance.size() != 0) {
                // Lấy student subject
                for (Attendance attendance : listAttendance) {
                    // Lấy Attendance
                    HttpEntity<String> studentSubjectRequest = new HttpEntity<>(headers);
                    ResponseEntity<ResponseModel> studentSubjectResponse = restTemplate.exchange(STUDENT_SUBJECT_URL + "getByAttendanceId/" + attendance.getStudentSubjectId(), HttpMethod.GET, studentSubjectRequest, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(studentSubjectResponse.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                    // Lấy student
                    HttpEntity<String> studentRequest = new HttpEntity<>(headers);
                    ResponseEntity<ResponseModel> studentResponse = restTemplate.exchange(STUDENT_URL + "get/" + studentSubject.getStudentId(), HttpMethod.GET, studentRequest, ResponseModel.class);
                    String studentJson = new ObjectMapper().writeValueAsString(studentResponse.getBody().getData());
                    Student student = new ObjectMapper().readValue(studentJson, Student.class);
                    AttendanceEdit editAttendance = new AttendanceEdit();
                    editAttendance.setAttendance_id(attendance.getId());
                    editAttendance.setStudent_id(student.getId());
                    editAttendance.setAvatar(student.getStudentByProfile().getAvartarUrl());
                    editAttendance.setStudent_name(student.getStudentByProfile().getFirstName() + " " + student.getStudentByProfile().getLastName());
                    editAttendance.setNote(attendance.getNote());
                    editAttendance.setIsPresent(attendance.getStatus());
                    listEditAttendance.add(editAttendance);
                }
                return listEditAttendance;
            } else {
                return new ResponseEntity<String>("Không có dữ liệu", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>("Không tìm thấy dữ liệu", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/updateAttendance")
    @ResponseBody
    public Object updateAttendance(@CookieValue(name = "_token", defaultValue = "") String _token,
                                   @RequestParam("attendModel") String attenModel,
                                   @RequestParam("classId") String classId,
                                   @RequestParam("date") String date) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listAttendance = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                // Lấy schedule
                HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass/" + classId, HttpMethod.GET, requestSchedule, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);

                //Lấy schedule detail
                MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                scheduleDetailContent.add("date", date);
                scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetail", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                ScheduleDetail scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);

                //Lấy attendance model
                List<AttendanceModel> attendanceModel = new ObjectMapper().readValue(attenModel, new TypeReference<List<AttendanceModel>>() {
                });
                for (AttendanceModel attend : attendanceModel) {
                    MultiValueMap<String, String> attenModelContent = new LinkedMultiValueMap<>();
                    attenModelContent.add("studentId", attend.getStudent_id());
                    attenModelContent.add("subjectId", String.valueOf(scheduleDetail.getSubjectId()));
                    HttpEntity<MultiValueMap<String, String>> requestStudentSubject = new HttpEntity<>(attenModelContent, headers);
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, requestStudentSubject, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                    Attendance attendance = new Attendance();
                    attendance.setStudentSubjectId(studentSubject.getId());
                    attendance.setNote(attend.getNote());
                    attendance.setStatus(attend.getStatus().equals("Present") ? 1 : 0);
                    attendance.setDate(date);
                    attendance.setId(attend.getAttendance_id());
                    listAttendance.add(attendance);
                }
                MultiValueMap<String, String> contentAttendance = new LinkedMultiValueMap<>();
                String attendanceJson = new ObjectMapper().writeValueAsString(listAttendance);
                contentAttendance.add("list_attendance", attendanceJson);
                HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(contentAttendance, headers);
                ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "saveAll", HttpMethod.POST, requestAttendance, ResponseModel.class);
                return responseAttendance.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
