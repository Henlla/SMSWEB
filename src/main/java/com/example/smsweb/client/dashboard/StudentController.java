package com.example.smsweb.client.dashboard;

import com.example.smsweb.mail.MailService;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Major;
import com.example.smsweb.models.Profile;
import com.example.smsweb.models.Province;
import com.example.smsweb.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private MailService mailService;


    @GetMapping("/dashboard/create-student")
    public String createStudent(Model model){
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
                                @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters  + numbers;
        Profile parseProfile = new ObjectMapper().readValue(profile,Profile.class);
        String accountName = StringUtils.removeAccent(parseProfile.getFirstName()+parseProfile.getLastName()).toLowerCase().replace(" ","")+parseProfile.getDob().replace("/","");
        String password = RandomStringUtils.random(8,0,combinedChars.length(),true,true,combinedChars.toCharArray());

//        mailService.sendMail(parseProfile.getEmail(),accountName,password);
        return "dashboard/student/create_student";
    }

}
