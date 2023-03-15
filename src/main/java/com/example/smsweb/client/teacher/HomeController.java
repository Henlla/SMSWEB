package com.example.smsweb.client.teacher;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.TeachingCurrenDate;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.News;
import com.example.smsweb.models.Profile;
import com.example.smsweb.models.Schedule;
import com.example.smsweb.models.ScheduleDetail;
import com.example.smsweb.models.Student;
import com.example.smsweb.models.Subject;
import com.example.smsweb.models.Teacher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/teacher")
public class HomeController {
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String SEMESTER_URL = "http://localhost:8080/api/semester/";
    private final String NEWS_URL = "http://localhost:8080/api/news/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    public RestTemplate restTemplate;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token") String _token,Authentication auth,Model model) {
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL");
                LocalDate currentDate = LocalDate.now();
                List<TeachingCurrenDate> lCurrenDates = new ArrayList<>();
                for(Classses classses:listClass){
                    for(Schedule schedule: classses.getSchedulesById()){
                        for(ScheduleDetail scheduleDetail: schedule.getScheduleDetailsById()){
                            if(LocalDate.parse(scheduleDetail.getDate()).equals(currentDate)){
                                TeachingCurrenDate currentDateTeaching = new TeachingCurrenDate();
                                if(classses.getShift().substring(0,1).equals("M")){
                                    if(scheduleDetail.getSlot().equals(1)){
                                        currentDateTeaching.setClassCode(classses.getClassCode());
                                        currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()));
                                        currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                        currentDateTeaching.setTime("7:30 - 9:30");
                                        currentDateTeaching.setStartTime("7:30");
                                    }else{
                                        currentDateTeaching.setClassCode(classses.getClassCode());
                                        currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()));
                                        currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                        currentDateTeaching.setTime("9:30 - 11:30");
                                        currentDateTeaching.setStartTime("9:30");

                                    }
                                }else if(classses.getShift().substring(0,1).equals("A")){
                                    if(scheduleDetail.getSlot().equals(1)){
                                        currentDateTeaching.setClassCode(classses.getClassCode());
                                        currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()));
                                        currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                        currentDateTeaching.setTime("12:30 - 15:30");
                                        currentDateTeaching.setStartTime("12:30");
                                    }else{
                                        currentDateTeaching.setClassCode(classses.getClassCode());
                                        currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()));
                                        currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                        currentDateTeaching.setTime("15:30 - 17:30");
                                        currentDateTeaching.setStartTime("15:30");
                                    }
                                }else{
                                    if(scheduleDetail.getSlot().equals(1)){
                                        currentDateTeaching.setClassCode(classses.getClassCode());
                                        currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()));
                                        currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                        currentDateTeaching.setTime("17:30 - 19:30");
                                        currentDateTeaching.setStartTime("17:30");
                                    }else{
                                        currentDateTeaching.setClassCode(classses.getClassCode());
                                        currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()));
                                        currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                        currentDateTeaching.setTime("19:30 - 21:30");
                                        currentDateTeaching.setStartTime("19:30");
                                    }
                                }
                                lCurrenDates.add(currentDateTeaching);
                            }
                        }
                    }
                }
                model.addAttribute("listCurrenTeachingDate", lCurrenDates);
                model.addAttribute("currentDate",currentDate.format(formatter));
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
    public String students(Model model, @CookieValue(name = "_token", defaultValue = "") String _token)
            throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "list", HttpMethod.GET, request,
                    String.class);
            List<Student> listStudent = new ObjectMapper().readValue(response.getBody(),
                    new TypeReference<List<Student>>() {
                    });
            model.addAttribute("students",
                    listStudent.stream().sorted((s1, s2) -> s2.getId().compareTo(s1.getId())).toList());
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
            return "teacherDashboard/major/marjor_index";
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
            log.error(ex.getMessage());
            return "redirect:/teacherDashboard/logout";
        }
    }

    @GetMapping("/classes")
    public String classes(Model model, @CookieValue(name = "_token", defaultValue = "") String _token)
            throws JsonProcessingException {

        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "list", HttpMethod.GET, request,
                String.class);
        List<Classses> classList = new ObjectMapper().readValue(response.getBody(),
                new TypeReference<List<Classses>>() {
                });

        model.addAttribute("classes", classList);
        return "teacherDashboard/class/class_index";
    }

    @GetMapping("class/{classCode}")
    public String class_details(Model model, @CookieValue(name = "_token", defaultValue = "") String _token,
            @PathVariable("classCode") String classCode) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classCode", classCode);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST,
                    request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classses = new ObjectMapper().readValue(json, Classses.class);
            model.addAttribute("class", classses);
            return "teacherDashboard/class/class_details";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/teacherDashboard/logout";
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
            @PathVariable("accountId")Integer accountId) {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("password", oldPass);
            params.add("newPassword", newPass);
            HttpEntity<MultiValueMap<String, Object>> request=  new HttpEntity<>(params,headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(ACCOUNT_URL+"changePassword/"+accountId, HttpMethod.PUT,request,ResponseModel.class);
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
}
