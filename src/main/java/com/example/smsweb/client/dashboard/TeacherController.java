package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.mail.Mail;
import com.example.smsweb.mail.MailService;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.MultipartConfig;
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
public class TeacherController {
    private final String PROVINCE_URL = "http://localhost:8080/api/provinces/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";

    @Autowired
    private MailService mailService;

    @GetMapping("dashboard/create-teacher")
    public String create_teacher(Model model){
        RestTemplate restTemplate = new RestTemplate();
        List<Province> provinces = restTemplate.getForObject(PROVINCE_URL ,ArrayList.class);
        model.addAttribute("provinces",provinces);
        return "dashboard/teacher/create_teacher";
    }

    @PostMapping("dashboard/create-teacher")
    public String create_teacher(@RequestParam("profile")String profile,
                                 @RequestParam("file") MultipartFile file,
                                 @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException, MessagingException {

        RestTemplate restTemplate = new RestTemplate();
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters  + numbers;
        Profile parseProfile = new ObjectMapper().readValue(profile,Profile.class);

        //generate accountName = first_name+ last_name + dob
        String accountName = StringUtils.removeAccent(parseProfile.getFirstName()+parseProfile.getLastName()).toLowerCase().replace(" ","")
                +parseProfile.getDob().replace("/","");
        String password = RandomStringUtils.random(8,0,combinedChars.length(),true,true,combinedChars.toCharArray());

        //Save Account
        Account account = new Account(accountName,password,3);
        String jsonAccount = new ObjectMapper().writeValueAsString(account);
        HttpHeaders headersAccount = new HttpHeaders();
        headersAccount.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headersAccount.set("Authorization","Bearer "+_token);
        MultiValueMap<String, String> paramsAccount = new LinkedMultiValueMap<>();
        paramsAccount.add("account", jsonAccount);
        HttpEntity<MultiValueMap<String, String>> requestAccount = new HttpEntity<>(paramsAccount, headersAccount);
        ResponseEntity<ResponseModel> responseAccount = restTemplate.exchange(ACCOUNT_URL, HttpMethod.POST, requestAccount, ResponseModel.class);
        String accountResponseToJson = new ObjectMapper().writeValueAsString(responseAccount.getBody().getData());
        Account accountResponse = new ObjectMapper().readValue(accountResponseToJson,Account.class);
        //----------------------------

        //Send mail
        Mail mail = new Mail();
        mail.setToMail(parseProfile.getEmail());
        mail.setSubject("Account student HKT SYSTEM");
        Map<String,Object> props = new HashMap<>();
        props.put("accountName",accountName);
        props.put("password",password);
        mail.setProps(props);
        mailService.sendHtmlMessage(mail);
        //-----------------------------

        //save profile
        HttpHeaders headersProfile = new HttpHeaders();
        headersProfile.set("Content-Type", "multipart/form-data");
        headersProfile.set("Authorization","Bearer "+_token);
        MultiValueMap<String, Object> paramsProfile = new LinkedMultiValueMap<>();
        parseProfile.setAccountId(accountResponse.getId());
        String jsonProfile = new ObjectMapper().writeValueAsString(parseProfile);
        paramsProfile.add("file", file.getResource());
        paramsProfile.add("profile", jsonProfile);
        HttpEntity<MultiValueMap<String, Object>> requestEntityProfile = new HttpEntity<>(paramsProfile, headersProfile);
        ResponseEntity<ResponseModel> responseProfile = restTemplate.exchange(PROFILE_URL, HttpMethod.POST, requestEntityProfile, ResponseModel.class);
        String profileResponseToJson = new ObjectMapper().writeValueAsString(responseProfile.getBody().getData());
        Profile profileResponse = new ObjectMapper().readValue(profileResponseToJson,Profile.class);
        //------------------------------

        //Save teacher
        HttpHeaders headerTeacher = new HttpHeaders();
        headerTeacher.set("Authorization","Bearer "+_token);
        MultiValueMap<String, String> paramsTeacher = new LinkedMultiValueMap<>();
        paramsTeacher.add("profileId",String.valueOf(profileResponse.getId()));
        HttpEntity<MultiValueMap<String, String>> requestEntityStudent = new HttpEntity<>(paramsTeacher,headerTeacher);
        ResponseEntity<ResponseModel> responseModelStudent = restTemplate.exchange(TEACHER_URL, HttpMethod.POST,requestEntityStudent,ResponseModel.class);
        String teacherResponseToJson = new ObjectMapper().writeValueAsString(responseModelStudent.getBody().getData());
        Teacher teacherResponse = new ObjectMapper().readValue(teacherResponseToJson,Teacher.class);
        //----------------------


        return "dashboard/teacher/create_teacher";
    }
    @GetMapping("/dashboard/index-teacher")
    public String index(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(TEACHER_URL+"list",HttpMethod.GET,request,String.class);
        List<Teacher> listTeacher = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Teacher>>(){});
        model.addAttribute("teachers",listTeacher.stream().sorted((s1,s2)-> s1.getId().compareTo(s2.getId())).toList());
        return "dashboard/teacher/teacher_index";
    }

    @GetMapping("/dashboard/teacher_details/{id}")
    public String student_details(Model model,@CookieValue(name = "_token", defaultValue = "") String _token,@PathVariable("id")Integer id) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(TEACHER_URL+"get/"+id,HttpMethod.GET,request,String.class);
        ResponseModel responseModel = objectMapper.readValue(response.getBody(),new TypeReference<ResponseModel>(){});
        String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
        Teacher teacher = objectMapper.readValue(convertToJson,Teacher.class);
        model.addAttribute("teacher",teacher);
        return "dashboard/teacher/teacher_details";
    }

    @PostMapping("/dashboard/teacher/reset_password")
    @ResponseBody
    public String reset_password(@RequestParam("id")Integer id,@RequestParam("email")String email,@CookieValue(name = "_token", defaultValue = "") String _token) throws MessagingException {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters  + numbers;
        String password = RandomStringUtils.random(8,0,combinedChars.length(),true,true,combinedChars.toCharArray());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization","Bearer "+_token);
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("password",password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params,header);
        ResponseEntity<String> response =  restTemplate.exchange(ACCOUNT_URL+"reset_password/"+id,HttpMethod.PUT,request,String.class);


        //Send mail
        Mail mail = new Mail();
        mail.setToMail(email);
        mail.setSubject("Account student HKT SYSTEM");
        Map<String,Object> props = new HashMap<>();
        props.put("password",password);
        mail.setProps(props);
        mailService.sendHtmlMessageResetPass(mail);
        //-----------------------------

        return "success";
    }
}
