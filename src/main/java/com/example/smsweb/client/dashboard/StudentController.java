package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.mail.Mail;
import com.example.smsweb.mail.MailService;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;
@Controller
@MultipartConfig
@Slf4j
public class StudentController {

    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String PROVINCE_URL = "http://localhost:8080/api/provinces/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String STUDENT_SUBJECT_URL = "http://localhost:8080/api/student-subject/";
    private final String STUDENT_MAJOR_URL = "http://localhost:8080/api/student-major/";

    @Autowired
    private MailService mailService;


    @GetMapping("/dashboard/student/create-student")
    public String createStudent(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseModel responseMajor = restTemplate.getForObject(MAJOR_URL + "list",ResponseModel.class);
        List<Province> provinces = restTemplate.getForObject(PROVINCE_URL ,ArrayList.class);
        String convertMajorToJson = objectMapper.writeValueAsString(responseMajor.getData());
        List<Major> majors = objectMapper.readValue(convertMajorToJson,new TypeReference<>(){});
        model.addAttribute("majors",majors);
        model.addAttribute("provinces",provinces);
        return "dashboard/student/create_student";
    }


    @PostMapping("/dashboard/student/create-student")
    @ResponseBody
    public String createStudent(@RequestParam("profile")String profile,
                                @RequestParam("majorId")Integer majorId,
                                @RequestParam("file") MultipartFile file,
                                @CookieValue(name = "_token", defaultValue = "") String _token) throws IOException, MessagingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

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
        Account account = new Account(accountName,password,2);
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
        //-------------------------

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

        //Send mail
        Mail mail = new Mail();
        mail.setToMail(parseProfile.getEmail());
        mail.setSubject("Account student HKT SYSTEM");
        String name = profileResponse.getFirstName()+" "+profileResponse.getLastName();
        Map<String,Object> props = new HashMap<>();
        props.put("accountName",accountName);
        props.put("password",password);
        props.put("fullname",name);
        mail.setProps(props);
        mailService.sendHtmlMessage(mail);
        //-----------------------------

        //Save student
        //generate studentCard
        HttpHeaders headerStudent = new HttpHeaders();
        headerStudent.set("Authorization","Bearer "+_token);
        String studentCard = StringUtils.randomStudentCard(numbers);
        MultiValueMap<String, String> paramsStudent = new LinkedMultiValueMap<>();
        paramsStudent.add("studentCard",studentCard);
        paramsStudent.add("profileId",String.valueOf(profileResponse.getId()));
        HttpEntity<MultiValueMap<String, String>> requestEntityStudent = new HttpEntity<>(paramsStudent,headerStudent);
        ResponseEntity<ResponseModel> responseModelStudent = restTemplate.exchange(STUDENT_URL, HttpMethod.POST,requestEntityStudent,ResponseModel.class);
        String studentResponseToJson = new ObjectMapper().writeValueAsString(responseModelStudent.getBody().getData());
        Student studentResponse = new ObjectMapper().readValue(studentResponseToJson,Student.class);
        //----------------------

        //Save student-Subject
        List<StudentSubject> studentSubjectList = new ArrayList<>();
        ResponseModel subjectsByMajor = restTemplate.getForObject(SUBJECT_URL+"findByMajorId/"+majorId,ResponseModel.class);
        String jsonSubjectMajor = new ObjectMapper().writeValueAsString(subjectsByMajor.getData());
        List<Subject> convertJsonSubject = new ObjectMapper().readValue(jsonSubjectMajor, new TypeReference<List<Subject>>() {
        });
        for(Subject subject : convertJsonSubject){
            StudentSubject studentSubject = new StudentSubject(subject.getId(),studentResponse.getId(),"");
            studentSubjectList.add(studentSubject);
        }
        String studentSubjectListToJson = new ObjectMapper().writeValueAsString(studentSubjectList);
        HttpHeaders headersStudentSubject = new HttpHeaders();
        headersStudentSubject.set("Content-Type", "multipart/form-data");
        headersStudentSubject.set("Authorization","Bearer "+_token);
        MultiValueMap<String, String> paramsStudentSubject = new LinkedMultiValueMap<>();
        paramsStudentSubject.add("student_subjectList",studentSubjectListToJson);
        HttpEntity<MultiValueMap<String, String>> requestEntityStudentSubject = new HttpEntity<>(paramsStudentSubject,headersStudentSubject);
        ResponseEntity<ResponseModel> responseModelStudentSubject= restTemplate.exchange(STUDENT_SUBJECT_URL, HttpMethod.POST,requestEntityStudentSubject,ResponseModel.class);
        //-------------------

        //save major-student
        MajorStudent majorStudent = new MajorStudent(majorId,studentResponse.getId());
        String jsonMajorStudent = new ObjectMapper().writeValueAsString(majorStudent);
        HttpHeaders headersMajorStudent = new HttpHeaders();
        headersMajorStudent.set("Content-Type", "multipart/form-data");
        headersMajorStudent.set("Authorization","Bearer "+_token);
        MultiValueMap<String, String> paramsMajorStudent = new LinkedMultiValueMap<>();
        paramsMajorStudent.add("student_major",jsonMajorStudent);
        HttpEntity<MultiValueMap<String, String>> requestEntityMajorStudent = new HttpEntity<>(paramsMajorStudent,headersMajorStudent);
        ResponseEntity<ResponseModel> responseStudentMajor= restTemplate.exchange(STUDENT_MAJOR_URL, HttpMethod.POST,requestEntityMajorStudent,ResponseModel.class);
        //---------
        log.info("FINISH create-student");
        return "success";
    }

    @GetMapping("/dashboard/student/index-student")
    public String index(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL+"list",HttpMethod.GET,request,String.class);
        List<Student> listStudent = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Student>>(){});
        List<Province> provinces = restTemplate.getForObject(PROVINCE_URL ,ArrayList.class);
        model.addAttribute("provinces",provinces);
        model.addAttribute("students",listStudent.stream().sorted((s1,s2)->s2.getId().compareTo(s1.getId())).toList());
       return "dashboard/student/student_index";
    }

    @GetMapping("/dashboard/student/student_details/{id}")
    @ResponseBody
    public Object student_details(@CookieValue(name = "_token", defaultValue = "") String _token,@PathVariable("id")Integer id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL+"get/"+id,HttpMethod.GET,request,String.class);
        ResponseModel responseModel = objectMapper.readValue(response.getBody(),new TypeReference<ResponseModel>(){});
        String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
        Student student = objectMapper.readValue(convertToJson,Student.class);
        return student;
    }

    @PostMapping("/dashboard/student/reset_password")
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


    @PostMapping("/dashboard/student/student_update")
    @ResponseBody
    public String student_update(@CookieValue(name = "_token", defaultValue = "") String _token,@RequestParam("profile")String profile) throws JsonProcessingException {
        if (_token.equals("")) {
            return "failed";
        }
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        Profile profileConvert = objectMapper.readValue(profile,Profile.class);
        HttpHeaders headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> requestGet = new HttpEntity<>(headersGet);
        ResponseEntity<ResponseModel> responseModel = restTemplate.exchange(PROFILE_URL+profileConvert.getId(),HttpMethod.GET,requestGet,ResponseModel.class);
        String convertModelToJson = objectMapper.writeValueAsString(responseModel.getBody().getData());
        Profile convertJsonToProfile = objectMapper.readValue(convertModelToJson, new TypeReference<Profile>() {});
        profileConvert.setAvatarPath(convertJsonToProfile.getAvatarPath());
        profileConvert.setAvartarUrl(convertJsonToProfile.getAvartarUrl());

        String paramsJson = objectMapper.writeValueAsString(profileConvert);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("profile", paramsJson);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<ResponseModel> response = restTemplate.exchange(PROFILE_URL, HttpMethod.PUT, request, ResponseModel.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return "success";
        } else {
            return "failed";
        }
    }

    @PostMapping("/dashboard/student/changeImg")
    @ResponseBody
    public String change_image(@CookieValue(name = "_token", defaultValue = "") String _token,
                               @RequestParam("file") MultipartFile file,@RequestParam("id") Integer id){
        if (_token.equals("")) {
            return "failed";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        headers.set("Authorization","Bearer "+_token);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", file.getResource());
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(PROFILE_URL+id, HttpMethod.PUT, request, ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return "failed";
        }
    }
}
