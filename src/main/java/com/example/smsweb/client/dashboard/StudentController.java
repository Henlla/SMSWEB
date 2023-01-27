package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.mail.Mail;
import com.example.smsweb.mail.MailService;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @GetMapping("/dashboard/create-student")
    public String createStudent(Model model,@CookieValue(name = "_token", defaultValue = "") String _token){
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        List<Major> majors = restTemplate.getForObject(MAJOR_URL + "list",ArrayList.class);
        List<Province> provinces = restTemplate.getForObject(PROVINCE_URL ,ArrayList.class);
        model.addAttribute("majors",majors);
        model.addAttribute("provinces",provinces);
        return "dashboard/student/create_student";
    }


    @PostMapping("/dashboard/create-student")
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
        List<Subject> subjectsByMajor = restTemplate.getForObject(SUBJECT_URL+"findByMajorId/"+majorId,List.class);
        String jsonSubjectMajor = new ObjectMapper().writeValueAsString(subjectsByMajor);
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

}
