package com.example.smsweb.client.teacher;

import com.example.smsweb.dto.*;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.Format;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@Slf4j
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
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String ATTENDANCE_TRACKING_URL = "http://localhost:8080/api/attendance_tracking/";
    private final String URL_FCM = "http://localhost:8080/fcm/";

    RestTemplate restTemplate;
    List<Schedule> listSchedule;
    List<ScheduleDetail> listScheduleDetail;
    List<Classses> listClass;
    List<Attendance> listAttendance;
    List<AttendanceView> listAttendanceView;
    List<StudentSubject> listStudentSubject;
    AttendanceView attendanceView;
    List<AttendanceEdit> listEditAttendance;
    List<Attendance> existsAttendance;

    String mSTime = "07:30", mETime = "11:30";
    String aSTime = "12:30", aETime = "17:30";
    String eSTime = "17:30", eETime = "21:30";

    LocalTime startTime;
    LocalTime endTime;
    LocalTime onTime;

    DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate startDate;
    LocalDate endDate;
    LocalDate currentDate;
    LocalDate previousDate;
    LocalDate detailDate;

    @GetMapping("/index")
    public Object getAttendanceDate(@CookieValue(name = "_token", defaultValue = "") String _token,
                                    Authentication auth, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                return "teacherDashboard/attendance/attendance";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            model.addAttribute("msg", "Don't have time table for today");
            return "teacherDashboard/attendance/attendance";
        }
    }

    @GetMapping("/getAttendanceByDate")
    @ResponseBody
    public Object attendance(@CookieValue(name = "_token", defaultValue = "") String _token,
                             Model model, Authentication auth,
                             @RequestParam("date") String date) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                listSchedule = new ArrayList<>();
                listScheduleDetail = new ArrayList<>();
                listStudentSubject = new ArrayList<>();
                listClass = new ArrayList<>();
                listAttendanceView = new ArrayList<>();
                listAttendance = new ArrayList<>();
                existsAttendance = new ArrayList<>();
                restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy User hiên tại
                Account teacherUser = (Account) auth.getPrincipal();
                ResponseEntity<Profile> profileResponse = restTemplate.exchange(PROFILE_URL + "get/" + teacherUser.getId(), HttpMethod.GET, request, Profile.class);
                ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, request, Teacher.class);

                MultiValueMap<String, String> content = new LinkedMultiValueMap<String, String>();
                content.add("date", date);
                content.add("teacherId", String.valueOf(teacherResponse.getBody().getId()));
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(content, headers);

                //Lấy Schedule Detail theo ngày
                ResponseEntity<ResponseModel> scheduleDetailResponse = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailsByDateAndTeacherId", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(scheduleDetailResponse.getBody().getData());
                listScheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, new TypeReference<List<ScheduleDetail>>() {
                });

                if (!listScheduleDetail.isEmpty()) {
                    for (ScheduleDetail scheduleDetail : listScheduleDetail) {
                        listStudentSubject.clear();
                        // Lấy schedule theo id
                        ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "get/" + scheduleDetail.getScheduleId(), HttpMethod.POST, request, ResponseModel.class);
                        String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);

                        // Lấy class theo schedule id
                        ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + schedule.getClassId(), HttpMethod.GET, request, ResponseModel.class);
                        String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                        Classses classes = new ObjectMapper().readValue(classJson, Classses.class);

                        // Lấy student subject
                        for (StudentClass studentClass : classes.getStudentClassById()) {
                            MultiValueMap<String, Integer> studentSubjectContent = new LinkedMultiValueMap<>();
                            studentSubjectContent.add("studentId", studentClass.getStudentId());
                            studentSubjectContent.add("subjectId", scheduleDetail.getSubjectId());
                            HttpEntity<MultiValueMap<String, Integer>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                            ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);
                            String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                            StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                            listStudentSubject.add(studentSubject);
                        }
                        // Lấy Attendance
                        for (StudentSubject studentSubject : listStudentSubject) {
                            MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                            attendanceContent.add("date", scheduleDetail.getDate());
                            attendanceContent.add("studentSubjectId", String.valueOf(studentSubject.getId()));
                            attendanceContent.add("slot", String.valueOf(scheduleDetail.getSlot()));
                            attendanceContent.add("shift", classes.getShift());
                            HttpEntity<MultiValueMap<String, String>> attendanceRequest = new HttpEntity<>(attendanceContent, headers);
                            ResponseEntity<ResponseModel> attendanceResponse = restTemplate.exchange(ATTENDANCE_URL + "findAttendancesByDateAndSlotAndStudentSubjectAndShift", HttpMethod.POST, attendanceRequest, ResponseModel.class);
                            String attendanceJson = new ObjectMapper().writeValueAsString(attendanceResponse.getBody().getData());
                            List<Attendance> listAttendance = new ObjectMapper().readValue(attendanceJson, new TypeReference<List<Attendance>>() {
                            });
                            if (listAttendance != null) {
                                existsAttendance.addAll(listAttendance);
                            }
                        }
                        if (existsAttendance.size() != 0) {
                            attendanceView = new AttendanceView();
                            String currentTime = Format.timeFormat(LocalTime.now().toString()).toString();
                            String shift = classes.getShift().substring(0, 1);
                            switch (shift) {
                                case "M":
                                    startTime = Format.timeFormat(mSTime);
                                    endTime = Format.timeFormat(mETime);
                                    onTime = Format.timeFormat(currentTime);
                                    if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                        attendanceView.setOnTime(1);
                                    } else {
                                        attendanceView.setOnTime(0);
                                    }
                                    break;
                                case "A":
                                    startTime = Format.timeFormat(aSTime);
                                    endTime = Format.timeFormat(aETime);
                                    onTime = Format.timeFormat(currentTime);
                                    if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                        attendanceView.setOnTime(1);
                                    } else {
                                        attendanceView.setOnTime(0);
                                    }
                                    break;
                                case "E":
                                    startTime = Format.timeFormat(eSTime);
                                    endTime = Format.timeFormat(eETime);
                                    onTime = Format.timeFormat(currentTime);
                                    if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                        attendanceView.setOnTime(1);
                                    } else {
                                        attendanceView.setOnTime(0);
                                    }
                                    break;
                            }

                            String attendanceDate = Format.dateFormat(scheduleDetail.getDate(), "dd/mm/yyyy");
                            String showClassName = classes.getClassCode() + " (" + attendanceDate + " - "
                                    + scheduleDetail.getSubjectBySubjectId().getSubjectName() + " - "
                                    + scheduleDetail.getSubjectBySubjectId().getSubjectCode() + " - "
                                    + "Slot " + scheduleDetail.getSlot() + " )";
                            attendanceView.setClass_name(showClassName);
                            attendanceView.setClass_id(classes.getId() + "-" + scheduleDetail.getSlot() + "-" + scheduleDetail.getDate());
                            attendanceView.setDate(scheduleDetail.getDate());
                            attendanceView.setIsAttendance(1);
                            attendanceView.setSlot(scheduleDetail.getSlot());
                            attendanceView.setShift(classes.getShift());
                            listAttendanceView.add(attendanceView);
                            existsAttendance.clear();
                        } else {
                            attendanceView = new AttendanceView();
                            String currentTime = Format.timeFormat(LocalTime.now().toString()).toString();
                            String shift = classes.getShift().substring(0, 1);
                            currentDate = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            previousDate = currentDate.minusDays(1);
                            detailDate = LocalDate.parse(LocalDate.parse(scheduleDetail.getDate()).format(formatDate));
                            switch (shift) {
                                case "M":
                                    startTime = Format.timeFormat(mSTime);
                                    endTime = Format.timeFormat(eSTime);
                                    onTime = Format.timeFormat(currentTime);
                                    if (detailDate.isEqual(previousDate)) {
                                        attendanceView.setOnTime(1);
                                    } else if (detailDate.isEqual(currentDate)) {
                                        if (onTime.isAfter(startTime)) {
                                            attendanceView.setOnTime(1);
                                        } else {
                                            if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                                attendanceView.setOnTime(1);
                                            } else {
                                                attendanceView.setOnTime(0);
                                            }
                                        }
                                    } else {
                                        if (detailDate.plusDays(1).isEqual(previousDate)) {
                                            attendanceView.setOnTime(1);
                                        } else {
                                            if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                                attendanceView.setOnTime(1);
                                            } else {
                                                attendanceView.setOnTime(0);
                                            }
                                        }
                                    }
                                    break;
                                case "A":
                                    startTime = Format.timeFormat(aSTime);
                                    endTime = Format.timeFormat(aETime);
                                    onTime = Format.timeFormat(currentTime);
                                    if (detailDate.isEqual(previousDate)) {
                                        attendanceView.setOnTime(1);
                                    } else if (detailDate.isEqual(currentDate)) {
                                        if (onTime.isAfter(startTime)) {
                                            attendanceView.setOnTime(1);
                                        } else {
                                            if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                                attendanceView.setOnTime(1);
                                            } else {
                                                attendanceView.setOnTime(0);
                                            }
                                        }
                                    } else {
                                        if (detailDate.plusDays(1).isEqual(previousDate)) {
                                            attendanceView.setOnTime(1);
                                        } else {
                                            if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                                attendanceView.setOnTime(1);
                                            } else {
                                                attendanceView.setOnTime(0);
                                            }
                                        }
                                    }
                                    break;
                                case "E":
                                    startTime = Format.timeFormat(eSTime);
                                    endTime = Format.timeFormat(eETime);
                                    onTime = Format.timeFormat(currentTime);
                                    if (detailDate.isEqual(previousDate)) {
                                        attendanceView.setOnTime(1);
                                    } else if (detailDate.isEqual(currentDate)) {
                                        if (onTime.isAfter(startTime)) {
                                            attendanceView.setOnTime(1);
                                        } else {
                                            if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                                attendanceView.setOnTime(1);
                                            } else {
                                                attendanceView.setOnTime(0);
                                            }
                                        }
                                    } else {
                                        if (detailDate.plusDays(1).isEqual(previousDate)) {
                                            attendanceView.setOnTime(1);
                                        } else {
                                            if (onTime.isAfter(startTime) && onTime.isBefore(endTime)) {
                                                attendanceView.setOnTime(1);
                                            } else {
                                                attendanceView.setOnTime(0);
                                            }
                                        }
                                    }
                                    break;
                            }

                            String attendanceDate = Format.dateFormat(scheduleDetail.getDate(), "dd/mm/yyyy");
                            String showClassName = classes.getClassCode() + " (" + attendanceDate + " - "
                                    + scheduleDetail.getSubjectBySubjectId().getSubjectName() + " - "
                                    + scheduleDetail.getSubjectBySubjectId().getSubjectCode() + " - "
                                    + "Slot " + scheduleDetail.getSlot() + " )";
                            attendanceView.setClass_name(showClassName);
                            attendanceView.setClass_id(classes.getId() + "-" + scheduleDetail.getSlot() + "-" + scheduleDetail.getDate());
                            attendanceView.setDate(scheduleDetail.getDate());
                            attendanceView.setIsAttendance(0);
                            attendanceView.setSlot(scheduleDetail.getSlot());
                            attendanceView.setShift(classes.getShift());
                            listAttendanceView.add(attendanceView);
                        }
                    }
                    Collections.sort(listAttendanceView, Collections.reverseOrder(Comparator.comparing(AttendanceView::getShift).thenComparing(AttendanceView::getDate)));
                    return listAttendanceView;
                } else {
                    return listAttendanceView;
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "error/error";
        }
    }

    @GetMapping("/listStudentInClass/{id}")
    @ResponseBody
    public Object listStudentInClass(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @PathVariable("id") String classId) {
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
            return new ResponseEntity<String>("Don't find any records", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/submitAttendance")
    @ResponseBody
    public Object submitAttendance(@CookieValue(name = "_token", defaultValue = "") String _token,
                                   @RequestParam("attendModel") String attenModel,
                                   @RequestParam("classId") String classId,
                                   @RequestParam("date") String date,
                                   @RequestParam("slot") String slot,
                                   Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listAttendance = new ArrayList<>();
                existsAttendance = new ArrayList<>();
                listStudentSubject = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy Schedule theo class
                MultiValueMap<String, String> contentClass = new LinkedMultiValueMap<>();
                contentClass.add("classId", classId);
                HttpEntity<MultiValueMap<String, String>> requestClass = new HttpEntity<>(contentClass, headers);
                ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass", HttpMethod.POST, requestClass, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                List<Schedule> scheduleList = new ObjectMapper().readValue(scheduleJson, new TypeReference<List<Schedule>>() {
                });

                ScheduleDetail scheduleDetail = new ScheduleDetail();
                for (Schedule schedule : scheduleList) {
                    startDate = LocalDate.parse(schedule.getStartDate(), formatDate);
                    endDate = LocalDate.parse(schedule.getEndDate(), formatDate);
                    currentDate = LocalDate.parse(LocalDate.now().format(formatDate));
                    if ((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && currentDate.isBefore(endDate)) {
                        // Lấy Schedule detail
                        MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                        scheduleDetailContent.add("date", date);
                        scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                        scheduleDetailContent.add("slot", slot);
                        HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                        ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailBySlot", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                        String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                        scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);
                    }
                }

                // Lấy class theo id
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
                String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                Classses classses = new ObjectMapper().readValue(classJson, Classses.class);

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
                    if (attend.getStatus().equals("Absent")) {
                        ResponseEntity<ResponseModel> responseStudent = restTemplate.exchange(STUDENT_URL + "get/" + studentSubject.getStudentId(), HttpMethod.GET, request, ResponseModel.class);
                        String json = new ObjectMapper().writeValueAsString(responseStudent.getBody().getData());
                        Student student = new ObjectMapper().readValue(json, Student.class);
                        if (!student.getStudentByProfile().getAccountByAccountId().getAccountDevices().isEmpty()) {
                            String tokenDevices = student.getStudentByProfile().getAccountByAccountId().getAccountDevices().stream().findFirst().get().getDeviceToken();
                            List<String> listDeviceToken = new ArrayList<>();
                            listDeviceToken.add(tokenDevices);
                            MulticastMessageRepresentation message = new MulticastMessageRepresentation();

                            DataNotification dataNotification = new DataNotification();
                            dataNotification.setContent("Today , you have absent " + scheduleDetail.getSubjectBySubjectId().getSubjectCode());
                            dataNotification.setAction("Attendance");

                            String jsonData = new ObjectMapper().writeValueAsString(dataNotification);
                            message.setTitle("Announcement");
                            message.setData(jsonData);
                            message.setRegistrationTokens(listDeviceToken);

                            String messageJson = new ObjectMapper().writeValueAsString(message);
                            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
                            params.add("message", messageJson);
                            HttpEntity<MultiValueMap<String, Object>> requestFCM = new HttpEntity<>(params, headers);
                            ResponseEntity<String> responseFCM = restTemplate.exchange(URL_FCM + "clients", HttpMethod.POST, requestFCM, String.class);

                        }
                    }
                    Attendance attendance = new Attendance();
                    attendance.setStudentSubjectId(studentSubject.getId());
                    attendance.setNote(attend.getNote());
                    attendance.setStatus(attend.getStatus().equals("Present") ? 1 : 0);
                    attendance.setDate(date);
                    attendance.setSlot(Integer.valueOf(attend.getSlot()));
                    attendance.setShift(classses.getShift());
                    listAttendance.add(attendance);
                }
                for (Attendance attendance : listAttendance) {
                    MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                    attendanceContent.add("date", date);
                    attendanceContent.add("slot", slot);
                    attendanceContent.add("studentSubjectId", String.valueOf(attendance.getStudentSubjectId()));
                    attendanceContent.add("shift", attendance.getShift());
                    HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(attendanceContent, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateAndSlotAndStudentSubjectAndShift", HttpMethod.POST, requestAttendance, ResponseModel.class);
                    String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
                    Attendance exitAttendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
                    if (exitAttendance != null) {
                        existsAttendance.add(exitAttendance);
                    }
                }
                if (existsAttendance.size() == 0) {
                    MultiValueMap<String, String> contentAttendance = new LinkedMultiValueMap<>();
                    String attendanceJson = new ObjectMapper().writeValueAsString(listAttendance);
                    contentAttendance.add("list_attendance", attendanceJson);
                    HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(contentAttendance, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "saveAll", HttpMethod.POST, requestAttendance, ResponseModel.class);
                    listStudentSubject.clear();
                    if (responseAttendance.getStatusCode().is2xxSuccessful()) {
                        for (Attendance attendance : listAttendance) {
                            // Lấy student subject
                            ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getById/" + attendance.getStudentSubjectId(), HttpMethod.GET, request, ResponseModel.class);
                            String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                            StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);

                            // Lấy subject
                            ResponseEntity<ResponseModel> responseSubject = restTemplate.exchange(SUBJECT_URL + "findOne/" + studentSubject.getSubjectId(), HttpMethod.GET, request, ResponseModel.class);
                            String subjectJson = new ObjectMapper().writeValueAsString(responseSubject.getBody().getData());
                            Subject subject = new ObjectMapper().readValue(subjectJson, Subject.class);

                            // Lấy Attendance
                            MultiValueMap<String, Integer> attendanceContent = new LinkedMultiValueMap<>();
                            attendanceContent.add("student_subject_id", attendance.getStudentSubjectId());
                            HttpEntity<MultiValueMap<String, Integer>> attendanceRequest = new HttpEntity<>(attendanceContent, headers);
                            ResponseEntity<ResponseModel> attendanceResponse = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByStudentSubjectId", HttpMethod.POST, attendanceRequest, ResponseModel.class);
                            String jsonAttendance = new ObjectMapper().writeValueAsString(attendanceResponse.getBody().getData());
                            List<Attendance> listAttendance = new ObjectMapper().readValue(jsonAttendance, new TypeReference<List<Attendance>>() {
                            });
                            double absentCount = listAttendance.stream().filter(a -> a.getStatus() == 0).count();
                            double countSlot = subject.getSlot();
                            int sumSlot = (int) Math.floor((absentCount / countSlot) * 100);
                            studentSubject.setStatus(String.valueOf(sumSlot));
                            String jsonStudentSubject = new ObjectMapper().writeValueAsString(studentSubject);
                            StudentSubject subjectStudent = new ObjectMapper().readValue(jsonStudentSubject, StudentSubject.class);
                            listStudentSubject.add(subjectStudent);
                        }
                        MultiValueMap<String, String> studentSubjectContent = new LinkedMultiValueMap<>();
                        String jsonStudentSubject = new ObjectMapper().writeValueAsString(listStudentSubject);
                        studentSubjectContent.add("student_subject", jsonStudentSubject);
                        HttpEntity<MultiValueMap<String, String>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                        restTemplate.exchange(STUDENT_SUBJECT_URL + "updateAll", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);

                        // Lấy profile
                        Account account = (Account) auth.getPrincipal();
                        ResponseEntity<Profile> responseProfile = restTemplate.exchange(PROFILE_URL + "get/" + account.getId(), HttpMethod.GET, request, Profile.class);

                        // Lấy teacher
                        ResponseEntity<Teacher> responseTeacher = restTemplate.exchange(TEACHER_URL + "getByProfile/" + responseProfile.getBody().getId(), HttpMethod.GET, request, Teacher.class);
                        AttendanceTracking attendanceTracking = new AttendanceTracking();
                        attendanceTracking.setTeacherId(responseTeacher.getBody().getId());
                        attendanceTracking.setDate(date);

                        MultiValueMap<String, String> trackingContent = new LinkedMultiValueMap<>();
                        trackingContent.add("attendance_tracking", new ObjectMapper().writeValueAsString(attendanceTracking));
                        HttpEntity<MultiValueMap<String, String>> trackingRequest = new HttpEntity<>(trackingContent, headers);
                        restTemplate.exchange(ATTENDANCE_TRACKING_URL + "saveTracking", HttpMethod.POST, trackingRequest, ResponseModel.class);
                        return responseAttendance.getBody().getData();
                    } else {
                        return new ResponseEntity<String>("Attendance fail", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    existsAttendance.clear();
                    return new ResponseEntity<String>("Slot " + slot + " this class had attendance", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Attendance fail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/findAttendanceByDate")
    @ResponseBody
    public Object findAttendanceByDate(@CookieValue(name = "_token") String _token,
                                       @RequestParam("date") String date,
                                       @RequestParam("classId") String classId,
                                       @RequestParam("slot") String slot) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listStudentSubject = new ArrayList<>();
                listEditAttendance = new ArrayList<>();
                listAttendance = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy student
                ResponseEntity<ResponseModel> responseStudent = restTemplate.exchange(STUDENT_CLASS_URL + "getStudentByClassCode/" + classId, HttpMethod.GET, request, ResponseModel.class);
                String studentJson = new ObjectMapper().writeValueAsString(responseStudent.getBody().getData());
                List<StudentClass> studentClassesList = new ObjectMapper().readValue(studentJson, new TypeReference<List<StudentClass>>() {
                });

                // Lấy Schedule theo class
                MultiValueMap<String, String> contentClass = new LinkedMultiValueMap<>();
                contentClass.add("classId", classId);
                HttpEntity<MultiValueMap<String, String>> requestClass = new HttpEntity<>(contentClass, headers);
                ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass", HttpMethod.POST, requestClass, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                List<Schedule> scheduleList = new ObjectMapper().readValue(scheduleJson, new TypeReference<List<Schedule>>() {
                });

                ScheduleDetail scheduleDetail = new ScheduleDetail();
                for (Schedule schedule : scheduleList) {
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate startDate = LocalDate.parse(schedule.getStartDate(), format);
                    LocalDate endDate = LocalDate.parse(schedule.getEndDate(), format);
                    LocalDate currentDate = LocalDate.parse(LocalDate.now().format(format));
                    if ((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && currentDate.isBefore(endDate)) {
                        // Lấy Schedule detail
                        MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                        scheduleDetailContent.add("date", date);
                        scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                        scheduleDetailContent.add("slot", slot);
                        HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                        ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailBySlot", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                        String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                        scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);
                    }
                }

                // Lấy class theo id
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
                String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                Classses classses = new ObjectMapper().readValue(classJson, Classses.class);

                // Lấy list student subject
                for (StudentClass studentClass : studentClassesList) {
                    MultiValueMap<String, String> studentSubjectContent = new LinkedMultiValueMap<>();
                    studentSubjectContent.add("studentId", String.valueOf(studentClass.getStudentId()));
                    studentSubjectContent.add("subjectId", String.valueOf(scheduleDetail.getSubjectId()));
                    HttpEntity<MultiValueMap<String, String>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                    listStudentSubject.add(studentSubject);
                }
                // Lấy attendance theo student subject slot va date
                for (StudentSubject studentSubject : listStudentSubject) {
                    MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                    attendanceContent.add("date", date);
                    attendanceContent.add("slot", slot);
                    attendanceContent.add("studentSubjectId", String.valueOf(studentSubject.getId()));
                    attendanceContent.add("shift", classses.getShift());
                    HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(attendanceContent, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateAndSlotAndStudentSubjectAndShift", HttpMethod.POST, requestAttendance, ResponseModel.class);
                    String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
                    Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
                    if (attendance != null) {
                        listAttendance.add(attendance);
                    }
                }
                if (listAttendance.size() != 0) {
                    // Lấy student subject
                    for (Attendance attendance : listAttendance) {
                        // Lấy Student Subject
                        ResponseEntity<ResponseModel> studentSubjectResponse = restTemplate.exchange(STUDENT_SUBJECT_URL + "getByAttendanceId/" + attendance.getStudentSubjectId(), HttpMethod.GET, request, ResponseModel.class);
                        String studentSubjectJson = new ObjectMapper().writeValueAsString(studentSubjectResponse.getBody().getData());
                        StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);

                        // Lấy student
                        ResponseEntity<ResponseModel> studentResponse = restTemplate.exchange(STUDENT_URL + "get/" + studentSubject.getStudentId(), HttpMethod.GET, request, ResponseModel.class);
                        String jsonStudent = new ObjectMapper().writeValueAsString(studentResponse.getBody().getData());
                        Student student = new ObjectMapper().readValue(jsonStudent, Student.class);

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
                    return new ResponseEntity<String>("Don't find record", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Don't find any records", HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/updateAttendance")
    @ResponseBody
    public Object updateAttendance(@CookieValue(name = "_token", defaultValue = "") String _token,
                                   @RequestParam("attendModel") String attenModel,
                                   @RequestParam("classId") String classId,
                                   @RequestParam("date") String date,
                                   @RequestParam("slot") String slot) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listAttendance = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy Schedule theo class
                MultiValueMap<String, String> contentClass = new LinkedMultiValueMap<>();
                contentClass.add("classId", classId);
                HttpEntity<MultiValueMap<String, String>> requestClass = new HttpEntity<>(contentClass, headers);
                ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass", HttpMethod.POST, requestClass, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                List<Schedule> scheduleList = new ObjectMapper().readValue(scheduleJson, new TypeReference<List<Schedule>>() {
                });

                ScheduleDetail scheduleDetail = new ScheduleDetail();
                for (Schedule schedule : scheduleList) {
                    startDate = LocalDate.parse(schedule.getStartDate(), formatDate);
                    endDate = LocalDate.parse(schedule.getEndDate(), formatDate);
                    currentDate = LocalDate.parse(LocalDate.now().format(formatDate));
                    if ((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && currentDate.isBefore(endDate)) {
                        // Lấy Schedule detail
                        MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                        scheduleDetailContent.add("date", date);
                        scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                        scheduleDetailContent.add("slot", slot);
                        HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                        ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailBySlot", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                        String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                        scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);
                    }
                }

                // Lấy class theo id
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
                String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                Classses classses = new ObjectMapper().readValue(classJson, Classses.class);

                //Lấy attendance model
                List<AttendanceModel> attendanceModel = new ObjectMapper().readValue(attenModel, new TypeReference<List<AttendanceModel>>() {
                });
                for (AttendanceModel attend : attendanceModel) {
                    // Lấy student subject
                    MultiValueMap<String, String> attenModelContent = new LinkedMultiValueMap<>();
                    attenModelContent.add("studentId", attend.getStudent_id());
                    attenModelContent.add("subjectId", String.valueOf(scheduleDetail.getSubjectId()));
                    HttpEntity<MultiValueMap<String, String>> requestStudentSubject = new HttpEntity<>(attenModelContent, headers);
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, requestStudentSubject, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                    if (attend.getStatus().equals("Absent")) {
                        ResponseEntity<ResponseModel> responseStudent = restTemplate.exchange(STUDENT_URL + "get/" + studentSubject.getStudentId(), HttpMethod.GET, request, ResponseModel.class);
                        String json = new ObjectMapper().writeValueAsString(responseStudent.getBody().getData());
                        Student student = new ObjectMapper().readValue(json, Student.class);
                        if (!student.getStudentByProfile().getAccountByAccountId().getAccountDevices().isEmpty()) {
                            String tokenDevices = student.getStudentByProfile().getAccountByAccountId().getAccountDevices().stream().findFirst().get().getDeviceToken();
                            List<String> listDeviceToken = new ArrayList<>();
                            listDeviceToken.add(tokenDevices);
                            MulticastMessageRepresentation message = new MulticastMessageRepresentation();

                            DataNotification dataNotification = new DataNotification();
                            dataNotification.setContent("Today , you have absent " + scheduleDetail.getSubjectBySubjectId().getSubjectCode());
                            dataNotification.setAction("Attendance");

                            String jsonData = new ObjectMapper().writeValueAsString(dataNotification);
                            message.setTitle("Announcement");
                            message.setData(jsonData);
                            message.setRegistrationTokens(listDeviceToken);

                            String messageJson = new ObjectMapper().writeValueAsString(message);
                            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
                            params.add("message", messageJson);
                            HttpEntity<MultiValueMap<String, Object>> requestFCM = new HttpEntity<>(params, headers);
                            ResponseEntity<String> responseFCM = restTemplate.exchange(URL_FCM + "clients", HttpMethod.POST, requestFCM, String.class);

                        }
                    }
                    Attendance attendance = new Attendance();
                    attendance.setStudentSubjectId(studentSubject.getId());
                    attendance.setNote(attend.getNote());
                    attendance.setStatus(attend.getStatus().equals("Present") ? 1 : 0);
                    attendance.setDate(date);
                    attendance.setId(attend.getAttendance_id());
                    attendance.setSlot(Integer.parseInt(slot));
                    attendance.setShift(classses.getShift());
                    listAttendance.add(attendance);
                }

                MultiValueMap<String, String> contentAttendance = new LinkedMultiValueMap<>();
                String attendanceJson = new ObjectMapper().writeValueAsString(listAttendance);
                contentAttendance.add("list_attendance", attendanceJson);
                HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(contentAttendance, headers);
                ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "saveAll", HttpMethod.POST, requestAttendance, ResponseModel.class);
                listStudentSubject.clear();
                for (Attendance attendance : listAttendance) {
                    // Lấy student subject
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getById/" + attendance.getStudentSubjectId(), HttpMethod.GET, request, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);

                    // Lấy subject
                    ResponseEntity<ResponseModel> responseSubject = restTemplate.exchange(SUBJECT_URL + "findOne/" + studentSubject.getSubjectId(), HttpMethod.GET, request, ResponseModel.class);
                    String subjectJson = new ObjectMapper().writeValueAsString(responseSubject.getBody().getData());
                    Subject subject = new ObjectMapper().readValue(subjectJson, Subject.class);

                    // Lấy Attendance
                    MultiValueMap<String, Integer> attendanceContent = new LinkedMultiValueMap<>();
                    attendanceContent.add("student_subject_id", attendance.getStudentSubjectId());
                    HttpEntity<MultiValueMap<String, Integer>> attendanceRequest = new HttpEntity<>(attendanceContent, headers);
                    ResponseEntity<ResponseModel> attendanceResponse = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByStudentSubjectId", HttpMethod.POST, attendanceRequest, ResponseModel.class);
                    String jsonAttendance = new ObjectMapper().writeValueAsString(attendanceResponse.getBody().getData());
                    List<Attendance> listAttendance = new ObjectMapper().readValue(jsonAttendance, new TypeReference<List<Attendance>>() {
                    });

                    double absentCount = listAttendance.stream().filter(a -> a.getStatus() == 0).count();
                    double countSlot = subject.getSlot();
                    int sumSlot = (int) Math.floor((absentCount / countSlot) * 100);
                    studentSubject.setStatus(String.valueOf(sumSlot));
                    String jsonStudentSubject = new ObjectMapper().writeValueAsString(studentSubject);
                    StudentSubject subjectStudent = new ObjectMapper().readValue(jsonStudentSubject, StudentSubject.class);
                    listStudentSubject.add(subjectStudent);
                }
                MultiValueMap<String, String> studentSubjectContent = new LinkedMultiValueMap<>();
                String jsonStudentSubject = new ObjectMapper().writeValueAsString(listStudentSubject);
                studentSubjectContent.add("student_subject", jsonStudentSubject);
                HttpEntity<MultiValueMap<String, String>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                restTemplate.exchange(STUDENT_SUBJECT_URL + "updateAll", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);

                return responseAttendance.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Update fail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/viewAttendance")
    @ResponseBody
    public Object viewAttendance(@CookieValue(name = "_token") String _token,
                                 @RequestParam("date") String date,
                                 @RequestParam("classId") String classId,
                                 @RequestParam("slot") String slot) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listStudentSubject = new ArrayList<>();
                listEditAttendance = new ArrayList<>();
                listAttendance = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy student
                ResponseEntity<ResponseModel> responseStudent = restTemplate.exchange(STUDENT_CLASS_URL + "getStudentByClassCode/" + classId, HttpMethod.GET, request, ResponseModel.class);
                String studentJson = new ObjectMapper().writeValueAsString(responseStudent.getBody().getData());
                List<StudentClass> studentClassesList = new ObjectMapper().readValue(studentJson, new TypeReference<List<StudentClass>>() {
                });

                // Lấy Schedule theo class
                MultiValueMap<String, String> contentClass = new LinkedMultiValueMap<>();
                contentClass.add("classId", classId);
                HttpEntity<MultiValueMap<String, String>> requestClass = new HttpEntity<>(contentClass, headers);
                ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass", HttpMethod.POST, requestClass, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                List<Schedule> scheduleList = new ObjectMapper().readValue(scheduleJson, new TypeReference<List<Schedule>>() {
                });


                ScheduleDetail scheduleDetail = new ScheduleDetail();
                for (Schedule schedule : scheduleList) {
                    startDate = LocalDate.parse(schedule.getStartDate(), formatDate);
                    endDate = LocalDate.parse(schedule.getEndDate(), formatDate);
                    currentDate = LocalDate.parse(LocalDate.now().format(formatDate));
                    if ((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && currentDate.isBefore(endDate)) {
                        // Lấy Schedule detail
                        MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                        scheduleDetailContent.add("date", date);
                        scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                        scheduleDetailContent.add("slot", slot);
                        HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                        ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailBySlot", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                        String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                        scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);
                    }
                }

                // Lấy class theo id
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
                String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                Classses classses = new ObjectMapper().readValue(classJson, Classses.class);

                // Lấy list student subject
                for (StudentClass studentClass : studentClassesList) {
                    MultiValueMap<String, String> studentSubjectContent = new LinkedMultiValueMap<>();
                    studentSubjectContent.add("studentId", String.valueOf(studentClass.getStudentId()));
                    studentSubjectContent.add("subjectId", String.valueOf(scheduleDetail.getSubjectId()));
                    HttpEntity<MultiValueMap<String, String>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                    listStudentSubject.add(studentSubject);
                }
                // Lấy attendance theo student subject slot va date
                for (StudentSubject studentSubject : listStudentSubject) {
                    MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                    attendanceContent.add("date", date);
                    attendanceContent.add("slot", slot);
                    attendanceContent.add("studentSubjectId", String.valueOf(studentSubject.getId()));
                    attendanceContent.add("shift", classses.getShift());
                    HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(attendanceContent, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateAndSlotAndStudentSubjectAndShift", HttpMethod.POST, requestAttendance, ResponseModel.class);
                    String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
                    Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
                    if (attendance != null) {
                        listAttendance.add(attendance);
                    }
                }
                if (listAttendance.size() != 0) {
                    // Lấy student subject
                    for (Attendance attendance : listAttendance) {

                        // Lấy Attendance
                        ResponseEntity<ResponseModel> studentSubjectResponse = restTemplate.exchange(STUDENT_SUBJECT_URL + "getByAttendanceId/" + attendance.getStudentSubjectId(), HttpMethod.GET, request, ResponseModel.class);
                        String studentSubjectJson = new ObjectMapper().writeValueAsString(studentSubjectResponse.getBody().getData());
                        StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);

                        // Lấy student
                        ResponseEntity<ResponseModel> studentResponse = restTemplate.exchange(STUDENT_URL + "get/" + studentSubject.getStudentId(), HttpMethod.GET, request, ResponseModel.class);
                        String jsonStudent = new ObjectMapper().writeValueAsString(studentResponse.getBody().getData());
                        Student student = new ObjectMapper().readValue(jsonStudent, Student.class);
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
                    return new ResponseEntity<String>("Don't find record", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Don't find any records", HttpStatus.NOT_FOUND);
        }
    }
}
