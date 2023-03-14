package com.example.smsweb.client.teacher;

import com.example.smsweb.dto.*;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.FormatDate;
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
    List<GroupingSchedule> listGroupingSchedule;
    List<Attendance> listExitAttendance;
    List<Attendance> existsAttendance;
    List<Integer> countAbsent;

    @GetMapping("/index")
    public String attendance(@CookieValue(name = "_token", defaultValue = "") String _token, Model model, Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                listSchedule = new ArrayList<>();
                listScheduleDetail = new ArrayList<>();
                listStudentSubject = new ArrayList<>();
                listClass = new ArrayList<>();
                listAttendanceView = new ArrayList<>();
                listGroupingSchedule = new ArrayList<>();
                listAttendance = new ArrayList<>();
                restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, String> content = new LinkedMultiValueMap<String, String>();
                String toDate = FormatDate.dateFormat(LocalDate.now(), "yyyy-mm-dd");
                String[] date = toDate.split("-");
                String fromDate = FormatDate.dateFormat(LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]) - 2), "yyyy-mm-dd");
                content.add("fromDate", fromDate);
                content.add("toDate", toDate);
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(content, headers);

                //Lấy Schedule Detail từ ngày đến ngày
                ResponseEntity<ResponseModel> scheduleDetailResponse = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailByDateBetween", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(scheduleDetailResponse.getBody().getData());
                listScheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, new TypeReference<List<ScheduleDetail>>() {
                });

                if (listScheduleDetail != null) {
                    for (ScheduleDetail scheduleDetail : listScheduleDetail) {
                        //Lấy schedule
                        HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                        ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "get/" + scheduleDetail.getScheduleId(), HttpMethod.POST, requestSchedule, ResponseModel.class);
                        String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                        listSchedule.add(schedule);
                    }
                    List<Integer> scheduleClass = listSchedule.stream().map(Schedule::getClassId).distinct().toList();
                    for (Integer scheduleClassId : scheduleClass) {
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
                        classContent.add("scheduleId", String.valueOf(scheduleClassId));
                        HttpEntity<MultiValueMap<String, String>> requestClass = new HttpEntity<>(classContent, headers);
                        ResponseEntity<ResponseModel> classResponse = restTemplate.exchange(CLASS_URL + "findClassByTeacherAndSchedule", HttpMethod.POST, requestClass, ResponseModel.class);
                        String classJon = new ObjectMapper().writeValueAsString(classResponse.getBody().getData());
                        Classses classses = new ObjectMapper().readValue(classJon, Classses.class);
                        listClass.add(classses);
                    }
                    for (Classses classes : listClass) {
                        // Lấy schedule theo class
                        HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                        ResponseEntity<ResponseModel> scheduleResponse = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassId/" + classes.getId(), HttpMethod.GET, requestSchedule, ResponseModel.class);
                        String scheduleJson = new ObjectMapper().writeValueAsString(scheduleResponse.getBody().getData());
                        Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                        String to_date = FormatDate.dateFormat(LocalDate.now(), "yyyy-mm-dd");
                        String[] formatDate = to_date.split("-");
                        String from_date = FormatDate.dateFormat(LocalDate.of(Integer.parseInt(formatDate[0]), Integer.parseInt(formatDate[1]), Integer.parseInt(formatDate[2]) - 2), "yyyy-mm-dd");
                        // Lấy schedule detail theo schedule
                        MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                        scheduleDetailContent.add("fromDate", from_date);
                        scheduleDetailContent.add("toDate", to_date);
                        scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                        HttpEntity<MultiValueMap<String, String>> scheduleDetailRequest = new HttpEntity<>(scheduleDetailContent, headers);
                        ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailByDateBetweenAndScheduleId", HttpMethod.POST, scheduleDetailRequest, ResponseModel.class);
                        String jsonScheduleDetail = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                        List<ScheduleDetail> scheduleDetailList = new ObjectMapper().readValue(jsonScheduleDetail, new TypeReference<List<ScheduleDetail>>() {
                        });
                        for (ScheduleDetail scheduleDetail : scheduleDetailList) {
                            //Lấy student trong class
                            HttpEntity<String> studentCLassContent = new HttpEntity<>(headers);
                            ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "getStudentByClassCode/" + classes.getId(), HttpMethod.GET, studentCLassContent, ResponseModel.class);
                            String studentClassJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                            List<StudentClass> studentClassList = new ObjectMapper().readValue(studentClassJson, new TypeReference<List<StudentClass>>() {
                            });
                            listStudentSubject = new ArrayList<>();
                            // Lấy student subject
                            for (StudentClass studentClass : studentClassList) {
                                MultiValueMap<String, Integer> studentSubjectContent = new LinkedMultiValueMap<>();
                                studentSubjectContent.add("studentId", studentClass.getStudentId());
                                studentSubjectContent.add("subjectId", scheduleDetail.getSubjectId());
                                HttpEntity<MultiValueMap<String, Integer>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                                ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);
                                String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                                StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                                listStudentSubject.add(studentSubject);
                            }
                            existsAttendance = new ArrayList<>();
                            // Lấy Attendance
                            for (StudentSubject studentSubject : listStudentSubject) {
                                MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                                attendanceContent.add("date", scheduleDetail.getDate());
                                attendanceContent.add("studentSubjectId", String.valueOf(studentSubject.getId()));
                                attendanceContent.add("slot", String.valueOf(scheduleDetail.getSlot()));
                                HttpEntity<MultiValueMap<String, String>> attendanceRequest = new HttpEntity<>(attendanceContent, headers);
                                ResponseEntity<ResponseModel> attendanceResponse = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateAndSlotAndStudentSubject", HttpMethod.POST, attendanceRequest, ResponseModel.class);
                                String attendanceJson = new ObjectMapper().writeValueAsString(attendanceResponse.getBody().getData());
                                List<Attendance> listAttendance = new ObjectMapper().readValue(attendanceJson, new TypeReference<List<Attendance>>() {
                                });
                                if (listAttendance != null) {
                                    existsAttendance.addAll(listAttendance);
                                } else {
                                    existsAttendance = new ArrayList<>();
                                }
                            }
                            if (existsAttendance.size() != 0) {
                                attendanceView = new AttendanceView();
                                String[] splitDate = scheduleDetail.getDate().split("-");
                                String attendanceDate = splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0];
                                String showClassName = classes.getClassCode() + " (" + attendanceDate + " - "
                                        + scheduleDetail.getSubjectBySubjectId().getSubjectName() + " - "
                                        + scheduleDetail.getSubjectBySubjectId().getSubjectCode() + " - "
                                        + "Slot " + scheduleDetail.getSlot() + " )";
                                attendanceView.setClass_name(showClassName);
                                attendanceView.setClass_id(classes.getId() + "-" + scheduleDetail.getSlot() + "-" + scheduleDetail.getDate());
                                attendanceView.setDate(scheduleDetail.getDate());
                                attendanceView.setIsAttendance(1);
                                attendanceView.setSlot(scheduleDetail.getSlot());
                                listAttendanceView.add(attendanceView);
                            } else {
                                attendanceView = new AttendanceView();
                                String[] splitDate = scheduleDetail.getDate().split("-");
                                String attendanceDate = splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0];
                                String showClassName = classes.getClassCode() + " (" + attendanceDate + " - "
                                        + scheduleDetail.getSubjectBySubjectId().getSubjectName() + " - "
                                        + scheduleDetail.getSubjectBySubjectId().getSubjectCode() + " - "
                                        + "Slot " + scheduleDetail.getSlot() + " )";
                                attendanceView.setClass_name(showClassName);
                                attendanceView.setClass_id(classes.getId() + "-" + scheduleDetail.getSlot() + "-" + scheduleDetail.getDate());
                                attendanceView.setDate(scheduleDetail.getDate());
                                attendanceView.setIsAttendance(0);
                                attendanceView.setSlot(scheduleDetail.getSlot());
                                listAttendanceView.add(attendanceView);
                            }
                        }
                    }
                    Collections.sort(listAttendanceView, Comparator.comparing(AttendanceView::getDate));
                    model.addAttribute("msg", "");
                    model.addAttribute("listClass", listAttendanceView);
                    return "teacherDashboard/attendance/attendance";
                } else {
                    model.addAttribute("msg", "Don't have time table for today");
                    return "teacherDashboard/attendance/attendance";
                }
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            return e.getMessage();
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
            return new ResponseEntity<String>("Không tìm thấy dữ liệu", HttpStatus.BAD_REQUEST);
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
                listExitAttendance = new ArrayList<>();
                listStudentSubject = new ArrayList<>();
                countAbsent = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);
                // Lấy schedule
                ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassId/" + classId, HttpMethod.GET, request, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);

                //Lấy schedule detail
                MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                scheduleDetailContent.add("date", date);
                scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                scheduleDetailContent.add("slot", slot);
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailBySlot", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
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
                    attendance.setSlot(Integer.valueOf(attend.getSlot()));
                    listAttendance.add(attendance);
                }
                for (Attendance attendance : listAttendance) {
                    MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                    attendanceContent.add("date", date);
                    attendanceContent.add("slot", slot);
                    attendanceContent.add("student_subject", String.valueOf(attendance.getStudentSubjectId()));
                    HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(attendanceContent, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateSlotStudentSubject", HttpMethod.POST, requestAttendance, ResponseModel.class);
                    String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
                    Attendance exitAttendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
                    if (exitAttendance != null) {
                        listExitAttendance.add(exitAttendance);
                    }
                }
                if (listExitAttendance.size() == 0) {
                    MultiValueMap<String, String> contentAttendance = new LinkedMultiValueMap<>();
                    String attendanceJson = new ObjectMapper().writeValueAsString(listAttendance);
                    contentAttendance.add("list_attendance", attendanceJson);
                    HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(contentAttendance, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "saveAll", HttpMethod.POST, requestAttendance, ResponseModel.class);
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
                            for (Attendance countAttendance : listAttendance) {
                                if (countAttendance.getStatus() == 0) {
                                    countAbsent.add(countAttendance.getStatus());
                                }
                            }
                            double absentCount = countAbsent.size();
                            double countSlot = subject.getSlot();
                            int sumSlot = (int) Math.floor((absentCount / countSlot) * 100);
                            studentSubject.setStatus(String.valueOf(sumSlot));
                            String jsonStudentSubject = new ObjectMapper().writeValueAsString(studentSubject);
                            StudentSubject subjectStudent = new ObjectMapper().readValue(jsonStudentSubject, StudentSubject.class);
                            listStudentSubject.add(subjectStudent);
                            countAbsent.clear();
                        }
                        MultiValueMap<String, String> studentSubjectContent = new LinkedMultiValueMap<>();
                        String jsonStudentSubject = new ObjectMapper().writeValueAsString(listStudentSubject);
                        studentSubjectContent.add("student_subject", jsonStudentSubject);
                        HttpEntity<MultiValueMap<String, String>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                        restTemplate.exchange(STUDENT_SUBJECT_URL + "updateAll", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);

                        // Lấy profile
                        Account account = (Account) auth.getPrincipal();
                        HttpEntity<String> profileRequest = new HttpEntity<>(headers);
                        ResponseEntity<Profile> responseProfile = restTemplate.exchange(PROFILE_URL + "get/" + account.getId(), HttpMethod.GET, request, Profile.class);
                        // Lấy teacher
                        ResponseEntity<Teacher> responseTeacher = restTemplate.exchange(TEACHER_URL + "getByProfile/" + responseProfile.getBody().getId(), HttpMethod.GET, request, Teacher.class);
                        AttendanceTracking attendanceTracking = new AttendanceTracking();
                        attendanceTracking.setTeacherId(responseTeacher.getBody().getId());
                        attendanceTracking.setDate(date);

                        MultiValueMap<String,String> trackingContent = new LinkedMultiValueMap<>();
                        trackingContent.add("attendance_tracking",new ObjectMapper().writeValueAsString(attendanceTracking));
                        HttpEntity<MultiValueMap<String,String>> trackingRequest = new HttpEntity<>(trackingContent,headers);
                        restTemplate.exchange(ATTENDANCE_TRACKING_URL + "saveTracking",HttpMethod.POST,trackingRequest,ResponseModel.class);

                        return responseAttendance.getBody().getData();
                    } else {
                        return new ResponseEntity<String>("Attendance fail", HttpStatus.BAD_REQUEST);
                    }
                } else {
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
                listStudent = new ArrayList<>();
                listEditAttendance = new ArrayList<>();
                listAttendance = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                // Lấy student
                HttpEntity<String> requestStudent = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseStudent = restTemplate.exchange(STUDENT_CLASS_URL + "getStudentByClassCode/" + classId, HttpMethod.GET, requestStudent, ResponseModel.class);
                String studentJson = new ObjectMapper().writeValueAsString(responseStudent.getBody().getData());
                List<StudentClass> studentClassesList = new ObjectMapper().readValue(studentJson, new TypeReference<List<StudentClass>>() {
                });
                // Lấy Schedule theo class
                HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassId/" + classId, HttpMethod.GET, requestSchedule, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);
                // Lấy Schedule detail
                MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                scheduleDetailContent.add("date", date);
                scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                scheduleDetailContent.add("slot", slot);
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailBySlot", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                ScheduleDetail scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);
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
                    attendanceContent.add("student_subject", String.valueOf(studentSubject.getId()));
                    HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(attendanceContent, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateSlotStudentSubject", HttpMethod.POST, requestAttendance, ResponseModel.class);
                    String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
                    Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
                    listAttendance.add(attendance);
                }
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
                countAbsent = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                // Lấy schedule
                HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassId/" + classId, HttpMethod.GET, requestSchedule, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);

                //Lấy schedule detail
                MultiValueMap<String, String> scheduleDetailContent = new LinkedMultiValueMap<>();
                scheduleDetailContent.add("date", date);
                scheduleDetailContent.add("scheduleId", String.valueOf(schedule.getId()));
                scheduleDetailContent.add("slot", slot);
                HttpEntity<MultiValueMap<String, String>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailBySlot", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                ScheduleDetail scheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, ScheduleDetail.class);

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
                    Attendance attendance = new Attendance();
                    attendance.setStudentSubjectId(studentSubject.getId());
                    attendance.setNote(attend.getNote());
                    attendance.setStatus(attend.getStatus().equals("Present") ? 1 : 0);
                    attendance.setDate(date);
                    attendance.setId(attend.getAttendance_id());
                    attendance.setSlot(Integer.parseInt(slot));
                    listAttendance.add(attendance);
                }
                MultiValueMap<String, String> contentAttendance = new LinkedMultiValueMap<>();
                String attendanceJson = new ObjectMapper().writeValueAsString(listAttendance);
                contentAttendance.add("list_attendance", attendanceJson);
                HttpEntity<MultiValueMap<String, String>> requestAttendance = new HttpEntity<>(contentAttendance, headers);
                ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "saveAll", HttpMethod.POST, requestAttendance, ResponseModel.class);

                for (Attendance attendance : listAttendance) {
                    // Lấy student subject
                    HttpEntity<String> requestStudentSubject = new HttpEntity<>(headers);
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getById/" + attendance.getStudentSubjectId(), HttpMethod.GET, requestStudentSubject, ResponseModel.class);
                    String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);

                    // Lấy subject
                    HttpEntity<String> requestSubject = new HttpEntity<>(headers);
                    ResponseEntity<ResponseModel> responseSubject = restTemplate.exchange(SUBJECT_URL + "findOne/" + studentSubject.getSubjectId(), HttpMethod.GET, requestSubject, ResponseModel.class);
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
                    for (Attendance countAttendance : listAttendance) {
                        if (countAttendance.getStatus() == 0) {
                            countAbsent.add(countAttendance.getStatus());
                        }
                    }
                    double absentCount = countAbsent.size();
                    double countSlot = subject.getSlot();
                    int sumSlot = (int) Math.floor((absentCount / countSlot) * 100);
                    studentSubject.setStatus(String.valueOf(sumSlot));
                    String jsonStudentSubject = new ObjectMapper().writeValueAsString(studentSubject);
                    StudentSubject subjectStudent = new ObjectMapper().readValue(jsonStudentSubject, StudentSubject.class);
                    listStudentSubject.add(subjectStudent);
                    countAbsent.clear();
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

}
