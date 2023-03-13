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

                // Lấy profile
                Account account = new Account();
                account = (Account) auth.getPrincipal();
                HttpEntity<String> profileRequest = new HttpEntity<>(headers);
                ResponseEntity<Profile> responseProfile = restTemplate.exchange(PROFILE_URL + "get/" + account.getId(), HttpMethod.GET, profileRequest, Profile.class);

                // Lấy Student
                HttpEntity<String> requestStudent = new HttpEntity<>(headers);
                ResponseEntity<Student> responseStudent = restTemplate.exchange(STUDENT_URL + "getByProfile/" + responseProfile.getBody().getId(), HttpMethod.GET, requestStudent, Student.class);

                // Lấy student subject
                HttpEntity<String> studentSubjectRequest = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getByStudentId/" + responseStudent.getBody().getId(), HttpMethod.GET, studentSubjectRequest, ResponseModel.class);
                String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                List<StudentSubject> listStudentSubject = new ObjectMapper().readValue(studentSubjectJson, new TypeReference<List<StudentSubject>>() {
                });

                // Lấy class by student
                HttpEntity<String> studentClassRequest = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "getClassByStudentId/" + responseStudent.getBody().getId(), HttpMethod.GET, studentClassRequest, ResponseModel.class);
                String studentClassJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                StudentClass studentClass = new ObjectMapper().readValue(studentClassJson, StudentClass.class);

                // Lấy major_student by student
                HttpEntity<String> majorStudentRequest = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> majorStudentResponse = restTemplate.exchange(STUDENT_MAJOR + "findMajorStudentByStudentId/" + studentClass.getStudentId(), HttpMethod.GET, majorStudentRequest, ResponseModel.class);
                String studentMajorJson = new ObjectMapper().writeValueAsString(majorStudentResponse.getBody().getData());
                MajorStudent majorStudent = new ObjectMapper().readValue(studentMajorJson, MajorStudent.class);

                //Lấy schedule
                MultiValueMap<String, Integer> scheduleContent = new LinkedMultiValueMap<>();
                scheduleContent.add("classId", studentClass.getClassId());
                HttpEntity<MultiValueMap<String, Integer>> scheduleRequest = new HttpEntity<>(scheduleContent, headers);
                ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass", HttpMethod.POST, scheduleRequest, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                List<Schedule> scheduleList = new ObjectMapper().readValue(scheduleJson, new TypeReference<List<Schedule>>() {
                });

                // Lấy subject
                MultiValueMap<String,Integer> subjectContent = new LinkedMultiValueMap<>();
                subjectContent.add("fromSemester",1);
                subjectContent.add("toSemester",scheduleList.size());
                subjectContent.add("majorId",majorStudent.getMajorId());
                HttpEntity<MultiValueMap<String,Integer>> subjectRequest = new HttpEntity<>(subjectContent,headers);
                ResponseEntity<ResponseModel> responseSubject = restTemplate.exchange(SUBJECT_URL + "findSubjectBySemesterIdAndMajorId",HttpMethod.POST,subjectRequest,ResponseModel.class);
                String subjectJson = new ObjectMapper().writeValueAsString(responseSubject.getBody().getData());
                listSubject = new ObjectMapper().readValue(subjectJson, new TypeReference<List<Subject>>() {});

                for (Subject subject : listSubject){
                    SubjectView subjectView = new SubjectView();
                    // Lấy student subject
                    MultiValueMap<String,Integer> studentSubjectContent = new LinkedMultiValueMap<>();
                    studentSubjectContent.add("subjectId",subject.getId());
                    studentSubjectContent.add("studentId",responseStudent.getBody().getId());
                    HttpEntity<MultiValueMap<String,Integer>> requestStudentSubject = new HttpEntity<>(studentSubjectContent,headers);
                    ResponseEntity<ResponseModel> studentSubjectResponse = restTemplate.exchange(STUDENT_SUBJECT_URL + "findStudentSubjectBySubjectIdAndStudentId", HttpMethod.POST,requestStudentSubject,ResponseModel.class);
                    String jsonStudentSubject = new ObjectMapper().writeValueAsString(studentSubjectResponse.getBody().getData());
                    StudentSubject studentSubject = new ObjectMapper().readValue(jsonStudentSubject, StudentSubject.class);
                    subjectView.setId(subject.getId());
                    subjectView.setSubject_name(subject.getSubjectName());
                    subjectView.setSubject_code(subject.getSubjectCode());
                    subjectView.setStatus(studentSubject.getStatus());
                    listSubjectView.add(subjectView);
                }
                model.addAttribute("student", responseStudent.getBody());
                model.addAttribute("listSubject", listSubjectView);
                return "student/attendance";
            } else {
                return "/login";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "/login";
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

                // Lấy student class
                HttpEntity<String> requestStudentClass = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "getClassByStudentId/" + studentId, HttpMethod.GET, requestStudentClass, ResponseModel.class);
                String studentClassJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                StudentClass studentClass = new ObjectMapper().readValue(studentClassJson, StudentClass.class);

                // Lấy class
                HttpEntity<String> requestClass = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + studentClass.getClassId(), HttpMethod.GET, requestClass, ResponseModel.class);
                String classJson = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                Classses classses = new ObjectMapper().readValue(classJson, Classses.class);

                // Lấy teacher
                HttpEntity<String> requestTeacher = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseTeacher = restTemplate.exchange(TEACHER_URL + "get/" + classses.getTeacherId(), HttpMethod.GET, requestTeacher, ResponseModel.class);
                String teacherJson = new ObjectMapper().writeValueAsString(responseTeacher.getBody().getData());
                Teacher teacher = new ObjectMapper().readValue(teacherJson, Teacher.class);

                // Lấy schedule
                HttpEntity<String> requestSchedule = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassId/" + studentClass.getClassId(), HttpMethod.GET, requestSchedule, ResponseModel.class);
                String scheduleJson = new ObjectMapper().writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = new ObjectMapper().readValue(scheduleJson, Schedule.class);

                // Lấy Schedule Detail
                MultiValueMap<String, Integer> scheduleDetailContent = new LinkedMultiValueMap<>();
                scheduleDetailContent.add("scheduleId", schedule.getId());
                scheduleDetailContent.add("subjectId", subjectId);
                HttpEntity<MultiValueMap<String, Integer>> requestScheduleDetail = new HttpEntity<>(scheduleDetailContent, headers);
                ResponseEntity<ResponseModel> responseScheduleDetail = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleDetailByScheduleIdAndSubjectId", HttpMethod.POST, requestScheduleDetail, ResponseModel.class);
                String scheduleDetailJson = new ObjectMapper().writeValueAsString(responseScheduleDetail.getBody().getData());
                List<ScheduleDetail> listScheduleDetail = new ObjectMapper().readValue(scheduleDetailJson, new TypeReference<List<ScheduleDetail>>() {
                });

                // Lấy student subject
                MultiValueMap<String, Integer> studentSubjectContent = new LinkedMultiValueMap<>();
                studentSubjectContent.add("studentId", studentId);
                studentSubjectContent.add("subjectId", subjectId);
                HttpEntity<MultiValueMap<String, Integer>> studentSubjectRequest = new HttpEntity<>(studentSubjectContent, headers);
                ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "getOne", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);
                String studentSubjectJson = new ObjectMapper().writeValueAsString(responseStudentSubject.getBody().getData());
                StudentSubject studentSubject = new ObjectMapper().readValue(studentSubjectJson, StudentSubject.class);

                for (ScheduleDetail scheduleDetail : listScheduleDetail) {
                    SumAttendance sumAttendance = new SumAttendance();
                    // Lấy attendance
                    MultiValueMap<String, String> attendanceContent = new LinkedMultiValueMap<>();
                    attendanceContent.add("date", scheduleDetail.getDate());
                    attendanceContent.add("slot", String.valueOf(scheduleDetail.getSlot()));
                    attendanceContent.add("student_subject", String.valueOf(studentSubject.getId()));
                    HttpEntity<MultiValueMap<String, String>> attendanceRequest = new HttpEntity<>(attendanceContent, headers);
                    ResponseEntity<ResponseModel> responseAttendance = restTemplate.exchange(ATTENDANCE_URL + "findAttendanceByDateSlotStudentSubject", HttpMethod.POST, attendanceRequest, ResponseModel.class);
                    String attendanceJson = new ObjectMapper().writeValueAsString(responseAttendance.getBody().getData());
                    Attendance attendance = new ObjectMapper().readValue(attendanceJson, Attendance.class);
                    if (attendance != null) {
                        sumAttendance.setDate(scheduleDetail.getDate());
                        sumAttendance.setSlot(scheduleDetail.getSlot());
                        sumAttendance.setClass_name(classses.getClassCode());
                        sumAttendance.setTeacher_name(teacher.getProfileByProfileId().getFirstName() + " " + teacher.getProfileByProfileId().getLastName());
                        sumAttendance.setStatus(String.valueOf(attendance.getStatus()));
                    } else {
                        sumAttendance.setDate(scheduleDetail.getDate());
                        sumAttendance.setSlot(scheduleDetail.getSlot());
                        sumAttendance.setClass_name(classses.getClassCode());
                        sumAttendance.setTeacher_name(teacher.getProfileByProfileId().getFirstName() + " " + teacher.getProfileByProfileId().getLastName());
                        sumAttendance.setStatus("FUTURE");
                    }
                    listSumAttendance.add(sumAttendance);
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
