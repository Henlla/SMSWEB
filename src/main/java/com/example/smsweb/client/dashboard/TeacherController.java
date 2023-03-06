package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.TeacherClassModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.mail.Mail;
import com.example.smsweb.mail.MailService;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@MultipartConfig
@Slf4j
public class TeacherController {
    private final String PROVINCE_URL = "http://localhost:8080/api/provinces/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String ROLE_URL = "http://localhost:8080/api/roles/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";

    @Autowired
    private MailService mailService;

    @GetMapping("dashboard/teacher/create-teacher")
    public String create_teacher(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            List<Province> provinces = restTemplate.getForObject(PROVINCE_URL, ArrayList.class);
            model.addAttribute("provinces", provinces);
            return "dashboard/teacher/create_teacher";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @PostMapping("dashboard/teacher/create-teacher")
    @ResponseBody
    public String create_teacher(@RequestParam("profile") String profile,
                                 @RequestParam("file") MultipartFile file,
                                 @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException, MessagingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
            String numbers = "1234567890";
            String combinedChars = capitalCaseLetters + lowerCaseLetters + numbers;
            Profile parseProfile = new ObjectMapper().readValue(profile, Profile.class);

            //generate accountName = first_name+ last_name + dob
            String accountName = StringUtils.removeAccent(parseProfile.getFirstName() + parseProfile.getLastName()).toLowerCase().replace(" ", "")
                    + parseProfile.getDob().replace("/", "");
            String password = RandomStringUtils.random(8, 0, combinedChars.length(), true, true, combinedChars.toCharArray());

            //Select role
            HttpHeaders headersRole = new HttpHeaders();
            headersRole.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> paramRole = new LinkedMultiValueMap<>();
            String roleName = "TEACHER";
            paramRole.add("role", roleName);
            HttpEntity<MultiValueMap<String, String>> requestRole = new HttpEntity<>(paramRole, headersRole);
            ResponseEntity<String> responseRole = restTemplate.exchange(ROLE_URL + "get", HttpMethod.POST, requestRole, String.class);
            Role role = new ObjectMapper().readValue(responseRole.getBody(), Role.class);

            //Save Account
            Account account = new Account(accountName, password, role.getId());
            String jsonAccount = new ObjectMapper().writeValueAsString(account);
            HttpHeaders headersAccount = new HttpHeaders();
            headersAccount.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headersAccount.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, String> paramsAccount = new LinkedMultiValueMap<>();
            paramsAccount.add("account", jsonAccount);
            HttpEntity<MultiValueMap<String, String>> requestAccount = new HttpEntity<>(paramsAccount, headersAccount);
            ResponseEntity<ResponseModel> responseAccount = restTemplate.exchange(ACCOUNT_URL, HttpMethod.POST, requestAccount, ResponseModel.class);
            String accountResponseToJson = new ObjectMapper().writeValueAsString(responseAccount.getBody().getData());
            Account accountResponse = new ObjectMapper().readValue(accountResponseToJson, Account.class);
            //----------------------------

            //save profile
            HttpHeaders headersProfile = new HttpHeaders();
            headersProfile.set("Content-Type", "multipart/form-data");
            headersProfile.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> paramsProfile = new LinkedMultiValueMap<>();
            parseProfile.setAccountId(accountResponse.getId());
            String jsonProfile = new ObjectMapper().writeValueAsString(parseProfile);
            paramsProfile.add("file", file.getResource());
            paramsProfile.add("profile", jsonProfile);
            HttpEntity<MultiValueMap<String, Object>> requestEntityProfile = new HttpEntity<>(paramsProfile, headersProfile);
            ResponseEntity<ResponseModel> responseProfile = restTemplate.exchange(PROFILE_URL, HttpMethod.POST, requestEntityProfile, ResponseModel.class);
            String profileResponseToJson = new ObjectMapper().writeValueAsString(responseProfile.getBody().getData());
            Profile profileResponse = new ObjectMapper().readValue(profileResponseToJson, Profile.class);
            //------------------------------

            //Send mail
            Mail mail = new Mail();
            mail.setToMail(parseProfile.getEmail());
            mail.setSubject("Account student HKT SYSTEM");
            String name = profileResponse.getFirstName() + " " + profileResponse.getLastName();
            Map<String, Object> props = new HashMap<>();
            props.put("accountName", accountName);
            props.put("password", password);
            props.put("fullname", name);
            mail.setProps(props);
            mailService.sendHtmlMessage(mail);
            //-----------------------------

            //Save teacher
            HttpHeaders headerTeacher = new HttpHeaders();
            headerTeacher.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, String> paramsTeacher = new LinkedMultiValueMap<>();
            paramsTeacher.add("profileId", String.valueOf(profileResponse.getId()));
            paramsTeacher.add("teacherCard", StringUtils.randomTeacherCard(numbers));
            HttpEntity<MultiValueMap<String, String>> requestEntityStudent = new HttpEntity<>(paramsTeacher, headerTeacher);
            ResponseEntity<ResponseModel> responseModelStudent = restTemplate.exchange(TEACHER_URL, HttpMethod.POST, requestEntityStudent, ResponseModel.class);
            String teacherResponseToJson = new ObjectMapper().writeValueAsString(responseModelStudent.getBody().getData());
            Teacher teacherResponse = new ObjectMapper().readValue(teacherResponseToJson, Teacher.class);
            //----------------------


            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @GetMapping("/dashboard/teacher/index-teacher")
    public String index(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(TEACHER_URL + "list", HttpMethod.GET, request, String.class);
            List<Teacher> listTeacher = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Teacher>>() {
            });
            List<Province> provinces = restTemplate.getForObject(PROVINCE_URL, ArrayList.class);
            model.addAttribute("provinces", provinces);
            model.addAttribute("teachers", listTeacher.stream().sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).toList());
            return "dashboard/teacher/teacher_index";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @GetMapping("/dashboard/teacher/teacher_details/{id}")
    @ResponseBody
    public Object teacher_details(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") Integer id) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<String> response = restTemplate.exchange(TEACHER_URL + "get/" + id, HttpMethod.GET, request, String.class);
            ResponseModel responseModel = objectMapper.readValue(response.getBody(), new TypeReference<ResponseModel>() {
            });
            String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
            Teacher teacher = objectMapper.readValue(convertToJson, Teacher.class);
            ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "findClassByTeacher/" + teacher.getId(), HttpMethod.GET, request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
            List<Classses> classsesList = new ObjectMapper().readValue(json, new TypeReference<List<Classses>>() {
            });
            return new TeacherClassModel(teacher,classsesList);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }

    @PostMapping("/dashboard/teacher/reset_password")
    @ResponseBody
    public String reset_password(@RequestParam("id") Integer id, @RequestParam("email") String email, @CookieValue(name = "_token", defaultValue = "") String _token) throws MessagingException {
        try {
            JWTUtils.checkExpired(_token);
            String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
            String numbers = "1234567890";
            String combinedChars = capitalCaseLetters + lowerCaseLetters + numbers;
            String password = RandomStringUtils.random(8, 0, combinedChars.length(), true, true, combinedChars.toCharArray());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "Bearer " + _token);
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("password", password);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, header);
            ResponseEntity<String> response = restTemplate.exchange(ACCOUNT_URL + "reset_password/" + id, HttpMethod.PUT, request, String.class);


            //Send mail
            Mail mail = new Mail();
            mail.setToMail(email);
            mail.setSubject("Account student HKT SYSTEM");
            Map<String, Object> props = new HashMap<>();
            props.put("password", password);
            mail.setProps(props);
            mailService.sendHtmlMessageResetPass(mail);
            //-----------------------------

            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @PostMapping("/dashboard/teacher/teacher_update")
    @ResponseBody
    public String teacher_update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestParam("profile") String profile) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            Profile profileConvert = objectMapper.readValue(profile, Profile.class);
            HttpHeaders headersGet = new HttpHeaders();
            headersGet.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> requestGet = new HttpEntity<>(headersGet);
            ResponseEntity<ResponseModel> responseModel = restTemplate.exchange(PROFILE_URL + profileConvert.getId(), HttpMethod.GET, requestGet, ResponseModel.class);
            String convertModelToJson = objectMapper.writeValueAsString(responseModel.getBody().getData());
            Profile convertJsonToProfile = objectMapper.readValue(convertModelToJson, new TypeReference<Profile>() {
            });
            profileConvert.setAvatarPath(convertJsonToProfile.getAvatarPath());
            profileConvert.setAvartarUrl(convertJsonToProfile.getAvartarUrl());

            String paramsJson = objectMapper.writeValueAsString(profileConvert);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("profile", paramsJson);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<ResponseModel> response = restTemplate.exchange(PROFILE_URL, HttpMethod.PUT, request, ResponseModel.class);
            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }

    @PostMapping("/dashboard/teacher/changeImg")
    @ResponseBody
    public String change_image(@CookieValue(name = "_token", defaultValue = "") String _token,
                               @RequestParam("file") MultipartFile file, @RequestParam("id") Integer id) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "multipart/form-data");
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("file", file.getResource());
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(PROFILE_URL + id, HttpMethod.PUT, request, ResponseModel.class);
            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }
}
