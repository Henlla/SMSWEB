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

import java.util.*;

@Controller
@MultipartConfig
public class StaffController {
    private final String PROVINCE_URL = "http://localhost:8080/api/provinces/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private final String STAFF_URL = "http://localhost:8080/api/staffs/";
    private final String ROLE_URL = "http://localhost:8080/api/roles/";

    @Autowired
    private MailService mailService;

    @GetMapping("dashboard/staff/create-staff")
    public String create_teacher(Model model) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        List<Province> provinces = restTemplate.getForObject(PROVINCE_URL , ArrayList.class);
        String rolesResponse = restTemplate.getForObject(ROLE_URL+"list" ,String.class);
        List<Role> roles = new ObjectMapper().readValue(rolesResponse,new TypeReference<>(){});
        model.addAttribute("provinces",provinces);
        model.addAttribute("roles",roles.stream().filter(role -> !role.getRoleName().equals("ADMIN") && !role.getRoleName().equals("STUDENT")&& !role.getRoleName().equals("TEACHER")).toList());
        return "dashboard/staff/create_staff";
    }

    @PostMapping("dashboard/staff/create-staff")
    public String create_staff(@RequestParam("profile")String profile,
                                 @RequestParam("roleId")Integer roleId,
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
        Account account = new Account(accountName,password,roleId);
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

        //Save staff
        HttpHeaders headerTeacher = new HttpHeaders();
        headerTeacher.set("Authorization","Bearer "+_token);
        MultiValueMap<String, String> paramsTeacher = new LinkedMultiValueMap<>();
        paramsTeacher.add("profileId",String.valueOf(profileResponse.getId()));
        HttpEntity<MultiValueMap<String, String>> requestEntityStudent = new HttpEntity<>(paramsTeacher,headerTeacher);
        ResponseEntity<ResponseModel> responseModelStudent = restTemplate.exchange(STAFF_URL, HttpMethod.POST,requestEntityStudent,ResponseModel.class);
        String teacherResponseToJson = new ObjectMapper().writeValueAsString(responseModelStudent.getBody().getData());
        Staff teacherResponse = new ObjectMapper().readValue(teacherResponseToJson,Staff.class);
        //----------------------


        return "dashboard/teacher/create_teacher";
    }

    @GetMapping("/dashboard/staff/index-staff")
    public String index(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(STAFF_URL+"list",HttpMethod.GET,request,String.class);
        List<Staff> listStaff = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Staff>>(){});
        List<Province> provinces = restTemplate.getForObject(PROVINCE_URL ,ArrayList.class);
        model.addAttribute("provinces",provinces);
        model.addAttribute("staffs",listStaff.stream().sorted((s1,s2)->s2.getId().compareTo(s1.getId())).toList());
        return "dashboard/staff/staff_index";
    }

    @GetMapping("/dashboard/staff/staff_details/{id}")
    @ResponseBody
    public Object staff_details(@CookieValue(name = "_token", defaultValue = "") String _token,@PathVariable("id")Integer id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(STAFF_URL+"get/"+id,HttpMethod.GET,request,String.class);
        ResponseModel responseModel = objectMapper.readValue(response.getBody(),new TypeReference<ResponseModel>(){});
        String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
        Staff staff = objectMapper.readValue(convertToJson,Staff.class);
        return staff;
    }

    @PostMapping("/dashboard/staff/reset_password")
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
    @PostMapping("/dashboard/staff/staff_update")
    @ResponseBody
    public String staff_update(@CookieValue(name = "_token", defaultValue = "") String _token,@RequestParam("profile")String profile) throws JsonProcessingException {
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

    @PostMapping("/dashboard/staff/changeImg")
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
