package com.example.smsweb.client.student;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.SubjectView;
import com.example.smsweb.dto.SumAttendance;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("student/attendance")
public class AttendanceClientController {
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String ATTENDANCE_URL = "http://localhost:8080/api/attendance/";
    private final String STUDENT_SUBJECT_URL = "http://localhost:8080/api/student-subject/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String STUDENT_MAJOR = "http://localhost:8080/api/student-major/";
    RestTemplate restTemplate;
    List<Subject> listSubject;
    List<SumAttendance> listSumAttendance;
    List<SubjectView> listSubjectView;

    @GetMapping("/index")
    public Object index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model, Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                listSubject = new ArrayList<>();
                listSubjectView = new ArrayList<>();
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy profile
                Account account = (Account) auth.getPrincipal();
                ResponseEntity<Profile> responseProfile = restTemplate.exchange(PROFILE_URL + "get/" + account.getId(), HttpMethod.GET, request, Profile.class);

                // Lấy Student
                ResponseEntity<Student> responseStudent = restTemplate.exchange(STUDENT_URL + "getByProfile/" + responseProfile.getBody().getId(), HttpMethod.GET, request, Student.class);

                // Lấy class by student
                ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "getClassesByStudentId/" + responseStudent.getBody().getId(), HttpMethod.GET, request, ResponseModel.class);
                String studentClassJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                List<StudentClass> studentClassList = new ObjectMapper().readValue(studentClassJson, new TypeReference<List<StudentClass>>() {
                });

                // Lấy class
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + studentClassList.get(0).getClassId(), HttpMethod.GET, request, ResponseModel.class);
                String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                Classses classses = new ObjectMapper().readValue(classJson, Classses.class);

                // Lấy major_student by student
                ResponseEntity<ResponseModel> majorStudentResponse = restTemplate.exchange(STUDENT_MAJOR + "findMajorStudentByStudentId/" + studentClassList.get(0).getStudentId(), HttpMethod.GET, request, ResponseModel.class);
                String studentMajorJson = new ObjectMapper().writeValueAsString(majorStudentResponse.getBody().getData());
                MajorStudent majorStudent = new ObjectMapper().readValue(studentMajorJson, MajorStudent.class);

                if (!classses.getSchedulesById().isEmpty()) {
                    // Lấy subject
                    MultiValueMap<String, Integer> subjectContent = new LinkedMultiValueMap<>();
                    subjectContent.add("fromSemester", 1);
                    subjectContent.add("toSemester", classses.getSchedulesById().size());
                    subjectContent.add("majorId", majorStudent.getMajorId());
                    HttpEntity<MultiValueMap<String, Integer>> subjectRequest = new HttpEntity<>(subjectContent, headers);
                    ResponseEntity<ResponseModel> responseSubject = restTemplate.exchange(SUBJECT_URL + "findSubjectBySemesterIdAndMajorId", HttpMethod.POST, subjectRequest, ResponseModel.class);
                    String subjectJson = new ObjectMapper().writeValueAsString(responseSubject.getBody().getData());
                    listSubject = new ObjectMapper().readValue(subjectJson, new TypeReference<List<Subject>>() {
                    });
                    for (Subject subject : listSubject) {
                        SubjectView subjectView = new SubjectView();
                        // Lấy student subject
                        MultiValueMap<String, Integer> studentSubjectContent = new LinkedMultiValueMap<>();
                        studentSubjectContent.add("subjectId", subject.getId());
                        studentSubjectContent.add("studentId", responseStudent.getBody().getId());
                        HttpEntity<MultiValueMap<String, Integer>> requestStudentSubject = new HttpEntity<>(studentSubjectContent, headers);
                        ResponseEntity<ResponseModel> studentSubjectResponse = restTemplate.exchange(STUDENT_SUBJECT_URL + "findStudentSubjectBySubjectIdAndStudentId", HttpMethod.POST, requestStudentSubject, ResponseModel.class);
                        String jsonStudentSubject = new ObjectMapper().writeValueAsString(studentSubjectResponse.getBody().getData());
                        StudentSubject studentSubject = new ObjectMapper().readValue(jsonStudentSubject, StudentSubject.class);
                        subjectView.setId(subject.getId());
                        subjectView.setSemester(subject.getSemesterId());
                        subjectView.setSubject_name(subject.getSubjectName());
                        subjectView.setSubject_code(subject.getSubjectCode());
                        subjectView.setStatus(studentSubject.getStatus());
                        listSubjectView.add(subjectView);
                    }
                }
                Collections.sort(listSubjectView, Comparator.comparing(SubjectView::getSemester).thenComparing(SubjectView::getId));
                model.addAttribute("student", responseStudent.getBody());
                model.addAttribute("listSubject", listSubjectView);
                return "student/attendance";
            } else {
                return "/login";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "error/error";
        }
    }

    @PostMapping("/findAttendance")
    @ResponseBody
    public Object findAttendance(@CookieValue(name = "_token", defaultValue = "") String _token,
                                 @RequestParam("subject_id") Integer subjectId,
                                 @RequestParam("student_id") Integer studentId) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listSumAttendance = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                // Lấy student class
                ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "getClassesByStudentId/" + studentId, HttpMethod.GET, request, ResponseModel.class);
                String studentClassJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                List<StudentClass> studentClassList = new ObjectMapper().readValue(studentClassJson, new TypeReference<List<StudentClass>>() {
                });

                // Lấy class
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + studentClassList.get(0).getClassId(), HttpMethod.GET, request, ResponseModel.class);
                String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                Classses classses = new ObjectMapper().readValue(classJson, Classses.class);

                // Lấy student subject
                MultiValueMap<String, Integer> studentSubjectContent = new LinkedMultiValueMap<>();
                studentSubjectContent.add("studentId", studentId);
                studentSubjectContent.add("subjectId", subjectId);
                HttpEntity<MultiValueMap<String, Integer>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);
                String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);
                for (Schedule schedule : classses.getSchedulesById()) {
                    for (ScheduleDetail scheduleDetail : schedule.getScheduleDetailsById()) {
                        if (scheduleDetail.getSubjectId().equals(subjectId)) {
                            SumAttendance sumAttendance = new SumAttendance();
                            // Lấy attendance
                            MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                            attendanceContent.add("date", scheduleDetail.getDate());
                            attendanceContent.add("slot", String.valueOf(scheduleDetail.getSlot()));
                            attendanceContent.add("studentSubjectId", String.valueOf(studentSubject.getId()));
                            attendanceContent.add("shift", classses.getShift());
                            HttpEntity<MultiValueMap<String, String>> attendanceRequest = new HttpEntity<>(attendanceContent, headers);
                            ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateAndSlotAndStudentSubjectAndShift", HttpMethod.POST, attendanceRequest, ResponseModel.class);
                            String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
                            Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
                            if (attendance != null) {
                                sumAttendance.setDate(scheduleDetail.getDate());
                                sumAttendance.setSlot(scheduleDetail.getSlot());
                                sumAttendance.setClass_name(classses.getClassCode());
                                sumAttendance.setTeacher_name(classses.getTeacher().getProfileByProfileId().getFirstName() + " " + classses.getTeacher().getProfileByProfileId().getLastName());
                                sumAttendance.setStatus(String.valueOf(attendance.getStatus()));
                            } else {
                                sumAttendance.setDate(scheduleDetail.getDate());
                                sumAttendance.setSlot(scheduleDetail.getSlot());
                                sumAttendance.setClass_name(classses.getClassCode());
                                sumAttendance.setTeacher_name(classses.getTeacher().getProfileByProfileId().getFirstName() + " " + classses.getTeacher().getProfileByProfileId().getLastName());
                                sumAttendance.setStatus("FUTURE");
                            }
                            listSumAttendance.add(sumAttendance);
                        }
                    }
                }
                return listSumAttendance;
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Don't find any records", HttpStatus.NOT_FOUND);
        }
    }

}
