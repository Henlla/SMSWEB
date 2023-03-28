package com.example.smsweb.client.teacher;

import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.dto.AttendanceTrackingChart;
import com.example.smsweb.dto.AttendanceTrackingModel;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.TeachingCurrenDate;
import com.example.smsweb.dto.teacher.InputMarkModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.ExcelExport.ImportMarkExport;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.News;
import com.example.smsweb.models.Profile;
import com.example.smsweb.models.Schedule;
import com.example.smsweb.models.ScheduleDetail;
import com.example.smsweb.models.Student;
import com.example.smsweb.models.Subject;
import com.example.smsweb.models.Teacher;
import com.example.smsweb.utils.StreamHelper;
import com.example.smsweb.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/teacher")
public class HomeController {
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String CLASS_SUBJECT_URL = "http://localhost:8080/api/class-subject/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String MARK_URL = "http://localhost:8080/api/mark/";
    private final String STUDENT_SUBJECT_URL = "http://localhost:8080/api/student-subject/";
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String SEMESTER_URL = "http://localhost:8080/api/semester/";
    private final String NEWS_URL = "http://localhost:8080/api/news/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private final String ATTENDANCE_TRACKING_URL = "http://localhost:8080/api/attendance_tracking/";
    public RestTemplate restTemplate;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token") String _token, Authentication auth, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.equalsIgnoreCase("token expired")) {
                HttpHeaders headers = new HttpHeaders();
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();
                headers.set("Authorization", "Bearer " + _token);
                Account teacherUser = (Account) auth.getPrincipal();
                //Get class
                HttpEntity<String> requestClass = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "list", HttpMethod.GET, requestClass, String.class);
                List<Classses> classList = objectMapper.readValue(response.getBody(), new TypeReference<>() {
                });
                classList = classList.stream()
                        .filter(p -> p.getTeacher().getProfileByProfileId().getAccountByAccountId().getUsername().equals(teacherUser.getUsername()))
                        .sorted(Comparator.comparingInt(Classses::getId))
                        .collect(Collectors.toList());
                //Get total students
                List<StudentClass> studentClassList = new ArrayList<>();
                for (Classses classs : classList) {
                    studentClassList.addAll(classs.getStudentClassById());
                }
                int totalStudent = studentClassList.stream()
                        .filter(StreamHelper.distinctByKey(StudentClass::getStudentId)).toList().size();
                // Lấy Profile
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<Profile> profileResponse = restTemplate.exchange(
                        PROFILE_URL + "get/" + teacherUser.getId(), HttpMethod.GET, request, Profile.class);
                // Lấy teacher theo profile id
                ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(
                        TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET,
                        request, Teacher.class);
                ResponseEntity<ResponseModel> responseScheduleDetails = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleByTeacher/" + teacherResponse.getBody().getId(), HttpMethod.GET, request, ResponseModel.class);

                String jsonScheduleDetails = objectMapper.writeValueAsString(responseScheduleDetails.getBody().getData());
                List<ScheduleDetail> scheduleDetails = objectMapper.readValue(jsonScheduleDetails, new TypeReference<List<ScheduleDetail>>() {
                });
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL");
                LocalDate currentDate = LocalDate.now();
                List<TeachingCurrenDate> lCurrenDates = new ArrayList<>();
                for (ScheduleDetail scheduleDetail : scheduleDetails) {
                    if (LocalDate.parse(scheduleDetail.getDate()).equals(currentDate)) {
                        ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "get/" + scheduleDetail.getScheduleId(), HttpMethod.POST, request, ResponseModel.class);
                        String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                        Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                        ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + schedule.getClassId(), HttpMethod.GET, request, ResponseModel.class);
                        String jsonClass = objectMapper.writeValueAsString(responseClass.getBody().getData());
                        Classses classses = objectMapper.readValue(jsonClass, Classses.class);
                        TeachingCurrenDate currentDateTeaching = new TeachingCurrenDate();
                        if (classses.getShift().substring(0, 1).equals("M")) {
                            if (scheduleDetail.getSlot().equals(1)) {
                                currentDateTeaching.setClassCode(classses.getClassCode());
                                currentDateTeaching.setDate(scheduleDetail.getDate());
                                currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                currentDateTeaching.setTime("7:30 - 9:30");
                                currentDateTeaching.setStartTime("7:30");
                            } else {
                                currentDateTeaching.setClassCode(classses.getClassCode());
                                currentDateTeaching.setDate(scheduleDetail.getDate());
                                currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                currentDateTeaching.setTime("9:30 - 11:30");
                                currentDateTeaching.setStartTime("9:30");

                            }
                        } else if (classses.getShift().substring(0, 1).equals("A")) {
                            if (scheduleDetail.getSlot().equals(1)) {
                                currentDateTeaching.setClassCode(classses.getClassCode());
                                currentDateTeaching.setDate(scheduleDetail.getDate());
                                currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                currentDateTeaching.setTime("12:30 - 15:30");
                                currentDateTeaching.setStartTime("12:30");
                            } else {
                                currentDateTeaching.setClassCode(classses.getClassCode());
                                currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()).toString());
                                currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                currentDateTeaching.setTime("15:30 - 17:30");
                                currentDateTeaching.setStartTime("15:30");
                            }
                        } else {
                            if (scheduleDetail.getSlot().equals(1)) {
                                currentDateTeaching.setClassCode(classses.getClassCode());
                                currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()).toString());
                                currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                currentDateTeaching.setTime("17:30 - 19:30");
                                currentDateTeaching.setStartTime("17:30");
                            } else {
                                currentDateTeaching.setClassCode(classses.getClassCode());
                                currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()).toString());
                                currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                currentDateTeaching.setTime("19:30 - 21:30");
                                currentDateTeaching.setStartTime("19:30");
                            }
                        }
                        lCurrenDates.add(currentDateTeaching);
                    }
                }
                int currentMonth = LocalDate.now().getMonthValue();
                int currentYear = LocalDate.now().getYear();
                DateTimeFormatter monthYear = DateTimeFormatter.ofPattern("yyyy-MM");
                String month = LocalDate.now().format(monthYear);
                ResponseEntity<ResponseModel> responseAttendanceTracking = restTemplate.exchange(ATTENDANCE_TRACKING_URL + "findByTeacherId/" + teacherResponse.getBody().getId(), HttpMethod.GET, request, ResponseModel.class);
                List<AttendanceTrackingModel> trackingList = new ArrayList<>();

                LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
                List<AttendanceTracking> listChart = new ArrayList<>();

                List<AttendanceTracking> attendanceTrackingList = new ArrayList<>();

                if (responseAttendanceTracking.getBody().getData() != null) {
                    String jsonAttendanceTracking = objectMapper.writeValueAsString(responseAttendanceTracking.getBody().getData());
                    attendanceTrackingList = objectMapper.readValue(jsonAttendanceTracking, new TypeReference<List<AttendanceTracking>>() {
                    });

                    listChart = attendanceTrackingList;
                    attendanceTrackingList = attendanceTrackingList.stream().filter(attendanceTracking -> LocalDate.parse(attendanceTracking.getDate()).getMonthValue() == currentMonth && LocalDate.parse(attendanceTracking.getDate()).getYear() == currentYear).collect(Collectors.toList());


                    for (LocalDate i = firstDayOfMonth; i.isBefore(lastDayOfMonth.plusDays(1)); i = i.plusDays(1)) {
                        LocalDate finalI = i;
                        List<AttendanceTracking> attendanceTracking = attendanceTrackingList.stream().filter(attendanceTracking1 -> LocalDate.parse(attendanceTracking1.getDate()).equals(finalI)).collect(Collectors.toList());
                        if (!attendanceTracking.isEmpty()) {
                            for (AttendanceTracking att : attendanceTracking) {
                                AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                                attendanceTrackingModel.setDate(att.getDate());
                                attendanceTrackingModel.setCount(1);
                                trackingList.add(attendanceTrackingModel);
                            }
                        } else {
                            AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                            attendanceTrackingModel.setDate(finalI.toString());
                            attendanceTrackingModel.setCount(0);
                            trackingList.add(attendanceTrackingModel);
                        }
                    }

                } else {
                    for (LocalDate i = firstDayOfMonth; i.isBefore(lastDayOfMonth.plusDays(1)); i = i.plusDays(1)) {
                        LocalDate finalI = i;
                        AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                        attendanceTrackingModel.setDate(finalI.toString());
                        attendanceTrackingModel.setCount(0);
                        trackingList.add(attendanceTrackingModel);
                    }
                }


                HashMap<String, List<AttendanceTrackingModel>> hashMap = new HashMap<String, List<AttendanceTrackingModel>>();
                for (AttendanceTrackingModel att : trackingList) {
                    String key = att.getDate();
                    if (hashMap.containsKey(key)) {
                        List<AttendanceTrackingModel> list = hashMap.get(key);
                        list.add(att);

                    } else {
                        List<AttendanceTrackingModel> list = new ArrayList<AttendanceTrackingModel>();
                        list.add(att);
                        hashMap.put(key, list);
                    }
                }

                List<AttendanceTrackingModel> list = new ArrayList<>();
                for (String key : hashMap.keySet()) {
                    AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                    attendanceTrackingModel.setDate(key);
                    List<AttendanceTrackingModel> trackingModels = hashMap.get(key);
                    int i = 0;
                    for (AttendanceTrackingModel att : trackingModels) {
                        if (att.getCount().equals(1)) {
                            i++;
                        }
                    }
                    attendanceTrackingModel.setCount(i);
                    list.add(attendanceTrackingModel);
                }

                List<AttendanceTrackingChart> chartList = new ArrayList<>();
                for (int i = 1; i <= 12; i++) {
                    AttendanceTrackingChart attendanceTrackingChart = new AttendanceTrackingChart();
//                    String monthValue = i < 10 ? String.valueOf("0"+i):String.valueOf(i);
                    int finalI = i;

                    attendanceTrackingChart.setX(StringUtils.theMonth(finalI - 1));
                    int total = 0;
                    List<AttendanceTracking> list1 = listChart.stream().filter(attendanceTracking -> LocalDate.parse(attendanceTracking.getDate()).getMonthValue() == finalI && LocalDate.parse(attendanceTracking.getDate()).getYear() == currentYear).toList();
                    if (!list1.isEmpty()) {
                        total = list1.size();
                        attendanceTrackingChart.setY(total * 2);
                    } else {
                        attendanceTrackingChart.setY(total);
                    }
                    chartList.add(attendanceTrackingChart);
                }

                list = list.stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).collect(Collectors.toList());
                model.addAttribute("currentMonthTotalAttendanceTracking", attendanceTrackingList.stream().count() * 2);
                model.addAttribute("listCurrenTeachingDate", lCurrenDates);
                model.addAttribute("month", month);
                model.addAttribute("chartList", new ObjectMapper().writeValueAsString(chartList));
                model.addAttribute("teacherId", teacherResponse.getBody().getId());
                model.addAttribute("listAttendanceCurrenMonth", new ObjectMapper().writeValueAsString(list));
                model.addAttribute("currentDate", currentDate.format(formatter));
                model.addAttribute("totalClass", classList.size());
                model.addAttribute("totalStudent", totalStudent);
                return "teacherDashboard/home";
            } else {
                return "redirect:/logout";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/logout";
        }
    }

    @GetMapping("/students")
    public String students(Model model,
                           Principal principal,
                           @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "list", HttpMethod.GET, request, String.class);
            List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
            });
            classList = classList.stream()
                    .filter(p -> p.getTeacher()
                            .getProfileByProfileId()
                            .getAccountByAccountId()
                            .getUsername()
                            .equals(principal.getName()))
                    .sorted(Comparator.comparingInt(Classses::getId))
                    .collect(Collectors.toList());
            List<StudentClass> studentClassList = new ArrayList<>();
            for (Classses classs : classList) {
                studentClassList.addAll(classs.getStudentClassById());
            }
            List<Student> studentList = new ArrayList<>();
            studentClassList.forEach(studentClass -> studentList.add(studentClass.getClassStudentByStudent()));

            List<Student> collect = studentList.stream()
                    .filter(StreamHelper.distinctByKey(Student::getId))
                    .collect(Collectors.toList());

            model.addAttribute("students", collect);

            return "teacherDashboard/student/student_index";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/teacherDashboard/logout";
        }
    }

    @GetMapping("/majors")
    public String majors(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ResponseModel responseModel = new ResponseModel();
            HttpHeaders headers = new HttpHeaders();
            responseModel = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
            model.addAttribute("listMajor", responseModel.getData());
            return "teacherDashboard/major/major_index";
        } catch (Exception e) {
            return "redirect:/teacherDashboard/login";
        }
    }

    @GetMapping("/subjects")
    public String subjects(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            JWTUtils.checkExpired(_token);
            ResponseModel listSubject = new ResponseModel();
            ResponseModel listMajor = new ResponseModel();
            ResponseModel listSemester = new ResponseModel();
            RestTemplate restTemplate = new RestTemplate();

            listSubject = restTemplate.getForObject(SUBJECT_URL + "list", ResponseModel.class);
            listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
            listSemester = restTemplate.getForObject(SEMESTER_URL + "list", ResponseModel.class);

            model.addAttribute("listSubject", listSubject.getData());
            model.addAttribute("listMajor", listMajor.getData());
            model.addAttribute("listSemester", listSemester.getData());
            model.addAttribute("subject", new Subject());
            return "teacherDashboard/subject/subject_index";
        } catch (Exception e) {
            return "redirect:/teacherDashboard/login";
        }
    }

    @GetMapping("/news")
    public String news(Model model,
                       @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL + "list", ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            List<News> newsList = new ObjectMapper().readValue(json, new TypeReference<List<News>>() {
            });
            model.addAttribute("news", newsList);
            return "teacherDashboard/news/new_index";
        } catch (Exception ex) {
            if (ex.getMessage().equalsIgnoreCase("Token expired")) {
                return "redirect:/login";
            }
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @GetMapping("/classes")
    public String classes(Model model,
                          @CookieValue(name = "_token", defaultValue = "") String _token,
                          Principal principal) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "list", HttpMethod.GET, request, String.class);
            List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Classses>>() {
            });
            classList = classList.stream()
                    .filter(p -> p.getTeacher()
                            .getProfileByProfileId()
                            .getAccountByAccountId()
                            .getUsername()
                            .equals(principal.getName()))
                    .sorted(Comparator.comparingInt(Classses::getId))
                    .collect(Collectors.toList());

            model.addAttribute("classes", classList);
            return "teacherDashboard/class/class_index";
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @GetMapping("class/{classCode}")
    public String class_details(Model model,
                                @CookieValue(name = "_token", defaultValue = "") String _token,
                                Principal principal,
                                @PathVariable("classCode") String classCode) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classCode", classCode);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST,
                    request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classModel = new ObjectMapper().readValue(json, Classses.class);
            if (classModel.getTeacher()
                    .getProfileByProfileId()
                    .getAccountByAccountId()
                    .getUsername()
                    .equals(principal.getName())) {
                model.addAttribute("class", classModel);
                return "teacherDashboard/class/class_details";
            } else {
                model.addAttribute("msg", "You have no permission");
                return "redirect:/teacher/classes";
            }
        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, @CookieValue(name = "_token", defaultValue = "") String _token,
                          Authentication auth) {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<Object>(headers);
            Account teacherUser = (Account) auth.getPrincipal();
            // Lấy Profile
            ResponseEntity<Profile> profileResponse = restTemplate.exchange(PROFILE_URL + "get/" + teacherUser.getId(),
                    HttpMethod.GET, request, Profile.class);
            // Lấy teacher theo profile id
            ResponseEntity<Teacher> teacherResponse = restTemplate.exchange(
                    TEACHER_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, request,
                    Teacher.class);
            ResponseEntity<ResponseModel> listClassResponse = restTemplate.exchange(
                    CLASS_URL + "findClassByTeacher/" + teacherResponse.getBody().getId(), HttpMethod.GET,
                    request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(listClassResponse.getBody().getData());
            List<Classses> listClass = objectMapper.readValue(json, new TypeReference<List<Classses>>() {
            });
            model.addAttribute("teacher", teacherResponse.getBody());
            model.addAttribute("classList", listClass);
            model.addAttribute("account", teacherUser);
            return "teacherDashboard/profile/profile";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/logout";
        }
    }

    @PostMapping("/change_password/{accountId}")
    @ResponseBody
    public Object change_password(@CookieValue(name = "_token", defaultValue = "") String _token,
                                  @RequestParam("oldPass") String oldPass,
                                  @RequestParam("newPass") String newPass,
                                  @PathVariable("accountId") Integer accountId) {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("password", oldPass);
            params.add("newPassword", newPass);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(ACCOUNT_URL + "changePassword/" + accountId, HttpMethod.PUT, request, ResponseModel.class);
            return "success";
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return "error";
            }
        }
    }


    @PostMapping("/getAttendanceTrackingByMonth")
    @ResponseBody
    public Object getAttendanceTrackingByMonth(@CookieValue(name = "_token", defaultValue = "") String _token,
                                               @RequestParam("month") String monthValue, @RequestParam("teacherId") Integer teacherId) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            int currentMonth = Integer.parseInt(String.valueOf(monthValue.split("-")[1].charAt(1)));
            int currentYear = Integer.parseInt(String.valueOf(monthValue.split("-")[0]));
            LocalDate now = LocalDate.parse(monthValue + "-01");

            LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
            ResponseEntity<ResponseModel> responseAttendanceTracking = restTemplate.exchange(ATTENDANCE_TRACKING_URL + "findByTeacherId/" + teacherId, HttpMethod.GET, request, ResponseModel.class);
            List<AttendanceTracking> attendanceTrackingList = new ArrayList<>();
            List<AttendanceTrackingModel> trackingList = new ArrayList<>();
            if (responseAttendanceTracking.getBody().getData() != null) {
                String jsonAttendanceTracking = objectMapper.writeValueAsString(responseAttendanceTracking.getBody().getData());
                attendanceTrackingList = objectMapper.readValue(jsonAttendanceTracking, new TypeReference<List<AttendanceTracking>>() {
                });
                attendanceTrackingList = attendanceTrackingList.stream().filter(attendanceTracking -> LocalDate.parse(attendanceTracking.getDate()).getMonthValue() == currentMonth).collect(Collectors.toList());


                trackingList = new ArrayList<>();
                for (LocalDate i = firstDayOfMonth; i.isBefore(lastDayOfMonth.plusDays(1)); i = i.plusDays(1)) {
                    LocalDate finalI = i;
                    List<AttendanceTracking> attendanceTracking = attendanceTrackingList.stream().filter(attendanceTracking1 -> LocalDate.parse(attendanceTracking1.getDate()).equals(finalI)).collect(Collectors.toList());
                    if (!attendanceTracking.isEmpty()) {
                        for (AttendanceTracking att : attendanceTracking) {
                            AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                            attendanceTrackingModel.setDate(att.getDate());
                            attendanceTrackingModel.setCount(1);
                            trackingList.add(attendanceTrackingModel);
                        }
                    } else {
                        AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                        attendanceTrackingModel.setDate(finalI.toString());
                        attendanceTrackingModel.setCount(0);
                        trackingList.add(attendanceTrackingModel);
                    }
                }
            } else {
                trackingList = new ArrayList<>();
                for (LocalDate i = firstDayOfMonth; i.isBefore(lastDayOfMonth.plusDays(1)); i = i.plusDays(1)) {
                    LocalDate finalI = i;
                    AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                    attendanceTrackingModel.setDate(finalI.toString());
                    attendanceTrackingModel.setCount(0);
                    trackingList.add(attendanceTrackingModel);
                }
            }


            HashMap<String, List<AttendanceTrackingModel>> hashMap = new HashMap<String, List<AttendanceTrackingModel>>();
            for (AttendanceTrackingModel att : trackingList) {
                String key = att.getDate();
                if (hashMap.containsKey(key)) {
                    List<AttendanceTrackingModel> list = hashMap.get(key);
                    list.add(att);

                } else {
                    List<AttendanceTrackingModel> list = new ArrayList<AttendanceTrackingModel>();
                    list.add(att);
                    hashMap.put(key, list);
                }
            }
            
            List<AttendanceTrackingModel> list = new ArrayList<>();
            for (String key : hashMap.keySet()) {
                AttendanceTrackingModel attendanceTrackingModel = new AttendanceTrackingModel();
                attendanceTrackingModel.setDate(key);
                List<AttendanceTrackingModel> trackingModels = hashMap.get(key);
                int i = 0;
                for (AttendanceTrackingModel att : trackingModels) {
                    if (att.getCount().equals(1)) {
                        i++;
                    }
                }
                attendanceTrackingModel.setCount(i);
                list.add(attendanceTrackingModel);
            }

            list = list.stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).collect(Collectors.toList());
            return list;
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return "error";
            }
        }
    }

    @GetMapping("/class/get-all-subject/{classId}")
    @ResponseBody
    public Object getAllSubjectByClassId(@CookieValue(name = "_token", defaultValue = "") String _token,
                                         @PathVariable("classId") int classId) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> requestGET;

            List<Student> studentList = new ArrayList<>();

            //Get Class by classId
            requestGET = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "getClass/" + classId,
                    HttpMethod.GET, requestGET, String.class);
            ResponseModel responseModel = new ObjectMapper().readValue(response.getBody(), new TypeReference<ResponseModel>() {
            });
            String json = new ObjectMapper().writeValueAsString(responseModel.getData());
            Classses classModel = new ObjectMapper().readValue(json, Classses.class);

            //filter ListSubjects
            List<Subject> subjects = classModel.getMajor().getSubjectsById().stream().sorted(Comparator.comparingInt(Subject::getId)).toList();
            //Convert to Json
            String subjectJson = new ObjectMapper().writeValueAsString(subjects);

            //Get Student by classId with StudentClass model
            for (StudentClass studentClass : classModel.getStudentClassById()) {
                Student student = studentClass.getClassStudentByStudent();
                studentList.add(student);
            }

            return subjectJson;

        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @PostMapping("/class/get-student-list")
    @ResponseBody
    public Object getStudentListSubject(@CookieValue(name = "_token", defaultValue = "") String _token,
                                        @RequestParam("classId") int classId,
                                        @RequestParam("subjectId") int subjectId) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> requestGET;

            List<Student> studentList = new ArrayList<>();
            List<InputMarkModel> inputMarkModelList = new ArrayList<>();

            //Get Class by classId
            requestGET = new HttpEntity<>(headers);
            ResponseEntity<String> responseStudent = restTemplate.exchange(CLASS_URL + "getClass/" + classId,
                    HttpMethod.GET, requestGET, String.class);
            ResponseModel responseModelStudent = new ObjectMapper().readValue(responseStudent.getBody(), new TypeReference<ResponseModel>() {
            });
            String jsonClass = new ObjectMapper().writeValueAsString(responseModelStudent.getData());
            Classses classModel = new ObjectMapper().readValue(jsonClass, Classses.class);

            //Get Subject by subjectId
            requestGET = new HttpEntity<>(headers);
            ResponseEntity<String> responseSubject = restTemplate.exchange(SUBJECT_URL + "getSubjectBySubjectId/" + subjectId,
                    HttpMethod.GET, requestGET, String.class);
            ResponseModel responseModelSubject = new ObjectMapper().readValue(responseSubject.getBody(), new TypeReference<ResponseModel>() {
            });
            String jsonSubject = new ObjectMapper().writeValueAsString(responseModelSubject.getData());
            Subject subject = new ObjectMapper().readValue(jsonSubject, Subject.class);


            //Get Student by classId with StudentClass model
            for (StudentClass studentClass : classModel.getStudentClassById()) {
                Student student = studentClass.getClassStudentByStudent();
                studentList.add(student);
                inputMarkModelList.add(new InputMarkModel(student, subject));
            }

            String inputMarkJson = new ObjectMapper().writeValueAsString(inputMarkModelList);

            return inputMarkJson;

        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @PostMapping("/class/input-mark")
    @ResponseBody
    public Object getStudentListSubject(@CookieValue(name = "_token", defaultValue = "") String _token,
                                        @RequestParam("teacherId") int teacherId,
                                        @RequestParam("mark_list") String mark_list,
                                        @RequestParam("classId") int classId) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            List<Mark> markList = new ArrayList<>();
            List<InputMarkModel> checkedInputMarkModelList = new ArrayList<>();

            List<InputMarkModel> inputMarkModelList = objectMapper.readValue(mark_list, new TypeReference<>() {});


            if (inputMarkModelList.size() != 0) {
                //Get class
                ResponseEntity<String> responseClass = restTemplate.exchange(
                        CLASS_URL + "getClass/" + classId,
                        HttpMethod.GET, request, String.class);
                ResponseModel responseModelClass = objectMapper.readValue(responseClass.getBody(), new TypeReference<>() {
                });
                String classJson = objectMapper.writeValueAsString(responseModelClass.getData());
                Classses classs = objectMapper.readValue(classJson, new TypeReference<>() {
                });

                for (InputMarkModel item : inputMarkModelList) {
                    //Get  Subject And Student add to new Model
                    ResponseEntity<Student> responseStudent = restTemplate.exchange(
                            STUDENT_URL + "findStudentByStudentCard/" +item.getStudentCode(),
                            HttpMethod.GET, request, Student.class);
                    Student student = responseStudent.getBody();
                    if (student == null){
                        throw new ErrorHandler("Not found student: "+item.getFullName());
                    }

                    ResponseEntity<ResponseModel> responseSubject = restTemplate.exchange(
                            SUBJECT_URL + "findSubjectBySubjectCode/" +item.getSubjectCode(),
                            HttpMethod.GET, request, ResponseModel.class);
                    ResponseModel responseModelSubject = responseSubject.getBody();
                    if (responseModelSubject.getData() == null){
                        throw new ErrorHandler("Not found subject: "+item.getSubjectName());
                    }
                    String jsonResponseModelSubject = objectMapper.writeValueAsString(responseModelSubject.getData());
                    Subject subject = objectMapper.readValue(jsonResponseModelSubject, new TypeReference<>() {
                    });

                    //add to new checkedInputMarkModelList
                    InputMarkModel inputMarkModel = new InputMarkModel(student, subject);
                    checkedInputMarkModelList.add(inputMarkModel);

                    //Check existed mark in database
                    ResponseEntity<Mark> responseMark = restTemplate.exchange(
                            MARK_URL + "findMarkByStudentSubjectId/" + inputMarkModel.getStudentSubjectId(),
                            HttpMethod.GET, request, Mark.class);
                    Mark body = responseMark.getBody();
                    if (body != null) {
                        throw new ErrorHandler(inputMarkModel.getFullName() + " already have a record of mark in " + inputMarkModel.getSubjectName() + "!");
                    }
                    //Check permission mark a student
                    if (classs.getStudentClassById().stream().filter(studentClass -> studentClass.getStudentId() == inputMarkModel.getStudentId()).collect(Collectors.toList()).size() == 0) {
                        throw new ErrorHandler("You have no permission to mark this student: " + inputMarkModel.getFullName());
                    }

                    //Check existed subject and subject existed in this class
                    if (classs.getMajor().getSubjectsById().stream().filter(s -> s.getId() == inputMarkModel.getSubjectId()).collect(Collectors.toList()).size() == 0) {
                        throw new ErrorHandler("You have no permission to mark this subject: " + inputMarkModel.getSubjectName());
                    }

                    markList.add(new Mark(0, item.getAsmMark(), item.getObjMark(), inputMarkModel.getStudentSubjectId(), null));
                }

                //Save markList
                content.add("markList", markList);
                HttpEntity<MultiValueMap<String, Object>> requestMark = new HttpEntity<>(content, headers);
                ResponseEntity<ResponseModel> responseMark = restTemplate.exchange(MARK_URL + "saveAll",
                        HttpMethod.POST, requestMark, ResponseModel.class);
                content.remove("markList");
                if (responseMark.getStatusCode().is2xxSuccessful()) {
                    return "success";
                } else {
                    throw new ErrorHandler("Save mark list failed");
                }
            } else {
                throw new ErrorHandler("Empty list");
            }
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }

    }

    @GetMapping("/class/download_template/{classId}/{subjectId}")
    @ResponseBody
    public void downloadTemplate(HttpServletResponse response,
                                 @CookieValue(name = "_token", defaultValue = "") String _token,
                                 @PathVariable("classId") int classId,
                                 @PathVariable("subjectId") int subjectId) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();

            List<InputMarkModel> inputMarkModelList = new ArrayList<>();

            ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL + "findOne/" + classId,
                    HttpMethod.GET, request, String.class);
            ResponseModel responseModel = objectMapper.readValue(responseClass.getBody(), new TypeReference<>() {
            });
            String convertToJson = objectMapper.writeValueAsString(responseModel.getData());

            Classses classModel = objectMapper.readValue(convertToJson, Classses.class);

            //Get Subject by subjectId
            request = new HttpEntity<>(headers);
            ResponseEntity<String> responseSubject = restTemplate.exchange(SUBJECT_URL + "getSubjectBySubjectId/" + subjectId,
                    HttpMethod.GET, request, String.class);
            ResponseModel responseModelSubject = new ObjectMapper().readValue(responseSubject.getBody(), new TypeReference<ResponseModel>() {
            });
            String jsonSubject = new ObjectMapper().writeValueAsString(responseModelSubject.getData());
            Subject subject = new ObjectMapper().readValue(jsonSubject, Subject.class);

            //Get Student by classId with StudentClass model
            for (StudentClass studentClass : classModel.getStudentClassById()) {
                Student student = studentClass.getClassStudentByStudent();
                inputMarkModelList.add(new InputMarkModel(student, subject));
            }

            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=import_mark_" + classModel.getClassCode() + ".xlsx";
            response.setHeader(headerKey, headerValue);

            ImportMarkExport importMarkExport = new ImportMarkExport(inputMarkModelList);
            importMarkExport.generateExcelFile(response);
            //return bytes;

        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }


}
