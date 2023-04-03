package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.*;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.Format;
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
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String URL_CLASS = "http://localhost:8080/api/classes/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String STUDENT_SUBJECT_URL = "http://localhost:8080/api/student-subject/";
    private final String ATTENDANCE_URL = "http://localhost:8080/api/attendance/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String URL_FCM = "http://localhost:8080/fcm/";
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String ATTENDANCE_TRACKING_URL = "http://localhost:8080/api/attendance_tracking/";

    RestTemplate restTemplate;

    LocalDate startDate;
    LocalDate endDate;
    LocalDate currentDate;
    DateTimeFormatter formatDate;

    List<Student> listStudent;
    List<AttendanceEdit> listEditAttendance;
    List<ScheduleDetail> listScheduleDetail;
    List<AttendanceView> listAttendanceView;
    List<Schedule> listSchedule;
    List<Classses> listClass;
    List<StudentClass> listStudentClass;
    List<Attendance> listAttendance;
    List<StudentSubject> listStudentSubject;
    List<Attendance> existsAttendance;
    AttendanceAdmin attendanceAdmin;

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
    public Object findScheduleDetailByDate(@CookieValue(name = "_token") String _token,
                                           @RequestParam("date") String date) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listScheduleDetail = new ArrayList<>();
                listSchedule = new ArrayList<>();
                listClass = new ArrayList<>();
                listAttendanceView = new ArrayList<>();

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
                    ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "get/" + scheduleDetail.getScheduleId(), HttpMethod.POST, request, ResponseModel.class);
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
                listStudentClass = new ArrayList<>();
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
                    attendanceAdmin = new AttendanceAdmin();
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
                        editAttendance.setStudent_card(student.getStudentCard());
                        listEditAttendance.add(editAttendance);
                    }
                    return listEditAttendance;
                } else {
                    for (StudentSubject studentSubject : listStudentSubject) {
                        // Lấy student
                        ResponseEntity<ResponseModel> studentResponse = restTemplate.exchange(STUDENT_URL + "get/" + studentSubject.getStudentId(), HttpMethod.GET, request, ResponseModel.class);
                        String jsonStudent = new ObjectMapper().writeValueAsString(studentResponse.getBody().getData());
                        Student student = new ObjectMapper().readValue(jsonStudent, Student.class);

                        AttendanceEdit editAttendance = new AttendanceEdit();
                        editAttendance.setAttendance_id(0);
                        editAttendance.setStudent_id(student.getId());
                        editAttendance.setAvatar(student.getStudentByProfile().getAvartarUrl());
                        editAttendance.setStudent_name(student.getStudentByProfile().getFirstName() + " " + student.getStudentByProfile().getLastName());
                        editAttendance.setNote("");
                        editAttendance.setIsPresent(1);
                        editAttendance.setStudent_card(student.getStudentCard());
                        listEditAttendance.add(editAttendance);
                    }
                    return listEditAttendance;
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Don't find any records", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/submitAttendance")
    @ResponseBody
    public Object submitAttendance(@CookieValue(name = "_token", defaultValue = "") String _token,
                                   @RequestParam("attendModel") String attenModel,
                                   @RequestParam("classId") String classId,
                                   @RequestParam("date") String date,
                                   @RequestParam("slot") String slot) {
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
                    formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                            List<String> listDeviceToken = new ArrayList<>();
                            String tokenDevices = student.getStudentByProfile().getAccountByAccountId().getAccountDevices().stream().findFirst().get().getDeviceToken();
                            listDeviceToken.add(tokenDevices);
                            MulticastMessageRepresentation message = new MulticastMessageRepresentation();

                            DataNotification dataNotification = new DataNotification();
                            String content = attend.getNote().isEmpty() ? Format.dateFormat(date,"dd/MM/yyyy") + " ," +  " you have absent " + scheduleDetail.getSubjectBySubjectId().getSubjectCode() : Format.dateFormat(date,"dd/MM/yyyy") + " ," +  attend.getNote();
                            dataNotification.setContent(content);
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
                    attendance.setId(attend.getAttendance_id());
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
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "updateAll", HttpMethod.POST, studentSubjectRequest, ResponseModel.class);
                    if (responseStudentSubject.getStatusCode().is2xxSuccessful()) {
                        if (existsAttendance.isEmpty()) {
                            AttendanceTracking attendanceTracking = new AttendanceTracking();
                            attendanceTracking.setTeacherId(scheduleDetail.getTeacherId());
                            attendanceTracking.setDate(date);
                            MultiValueMap<String, String> trackingContent = new LinkedMultiValueMap<>();
                            trackingContent.add("attendance_tracking", new ObjectMapper().writeValueAsString(attendanceTracking));
                            HttpEntity<MultiValueMap<String, String>> trackingRequest = new HttpEntity<>(trackingContent, headers);
                            ResponseEntity<ResponseModel> responseAttendanceTracking = restTemplate.exchange(ATTENDANCE_TRACKING_URL + "saveTracking", HttpMethod.POST, trackingRequest, ResponseModel.class);
                            if (responseAttendanceTracking.getStatusCode().is2xxSuccessful()) {
                                return new ResponseEntity<String>("Attendance success", HttpStatus.OK);
                            } else {
                                return new ResponseEntity<String>("Attendance fail", HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            return new ResponseEntity<String>("Attendance success", HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<String>("Attendance fail", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<String>("Attendance fail", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Attendance fail", HttpStatus.BAD_REQUEST);
        }
    }
}
