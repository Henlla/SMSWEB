package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.StudentClassModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.mail.Mail;
import com.example.smsweb.mail.MailService;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.ExcelExport.StudentExport;
import com.example.smsweb.utils.ExcelHelper;
import com.example.smsweb.utils.FileUtils;
import com.example.smsweb.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.plexus.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final String ROLE_URL = "http://localhost:8080/api/roles/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";

    @Autowired
    private MailService mailService;


    List<Student> listStudent;

    XSSFWorkbook workbook;
    XSSFSheet sheet;

    @GetMapping("/dashboard/student/create-student")
    public String createStudent(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseModel responseMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
            List<Province> provinces = restTemplate.getForObject(PROVINCE_URL, ArrayList.class);
            String convertMajorToJson = objectMapper.writeValueAsString(responseMajor.getData());
            List<Major> majors = objectMapper.readValue(convertMajorToJson, new TypeReference<>() {
            });
            model.addAttribute("majors", majors);
            model.addAttribute("provinces", provinces);
            return "dashboard/student/create_student";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }


    @PostMapping("/dashboard/student/create-student")
    @ResponseBody
    public String createStudent(@RequestParam("profile") String profile,
                                @RequestParam("majorId") Integer majorId,
                                @RequestParam("file") MultipartFile file,
                                @CookieValue(name = "_token", defaultValue = "") String _token) throws IOException, MessagingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
            String numbers = "1234567890";
            String combinedChars = capitalCaseLetters + lowerCaseLetters + numbers;
            Profile parseProfile = new ObjectMapper().readValue(profile, Profile.class);

            String studentCard = StringUtils.randomStudentCard(numbers);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "findStudentCard/" + studentCard, HttpMethod.GET, request, String.class);
            if (response.getBody() != null) {
                studentCard = StringUtils.randomStudentCard(numbers);
            }

            //generate accountName = first_name+ last_name + dob
//            String accountName = StringUtils.removeAccent(parseProfile.getFirstName()+parseProfile.getLastName()).toLowerCase().replace(" ","")
//                    +parseProfile.getDob().replace("/","");
            String password = RandomStringUtils.random(8, 0, combinedChars.length(), true, true, combinedChars.toCharArray());


            //Select role
            HttpHeaders headersRole = new HttpHeaders();
            headersRole.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> paramRole = new LinkedMultiValueMap<>();
            String roleName = "STUDENT";
            paramRole.add("role", roleName);
            HttpEntity<MultiValueMap<String, String>> requestRole = new HttpEntity<>(paramRole, headersRole);
            ResponseEntity<String> responseRole = restTemplate.exchange(ROLE_URL + "get", HttpMethod.POST, requestRole, String.class);
            Role role = new ObjectMapper().readValue(responseRole.getBody(), Role.class);

            //Save Account
            Account account = new Account(studentCard.toLowerCase(), password, role.getId());

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
            //-------------------------

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
            props.put("accountName", accountResponse.getUsername());
            props.put("password", password);
            props.put("fullname", name);
            mail.setProps(props);
            mailService.sendHtmlMessage(mail);
            //-----------------------------

            //Save student
            //generate studentCard
            HttpHeaders headerStudent = new HttpHeaders();
            headerStudent.set("Authorization", "Bearer " + _token);

            MultiValueMap<String, String> paramsStudent = new LinkedMultiValueMap<>();
            paramsStudent.add("studentCard", studentCard);
            paramsStudent.add("profileId", String.valueOf(profileResponse.getId()));
            HttpEntity<MultiValueMap<String, String>> requestEntityStudent = new HttpEntity<>(paramsStudent, headerStudent);
            ResponseEntity<ResponseModel> responseModelStudent = restTemplate.exchange(STUDENT_URL, HttpMethod.POST, requestEntityStudent, ResponseModel.class);
            String studentResponseToJson = new ObjectMapper().writeValueAsString(responseModelStudent.getBody().getData());
            Student studentResponse = new ObjectMapper().readValue(studentResponseToJson, Student.class);
            //----------------------

//            //Save student-Subject
//            List<StudentSubject> studentSubjectList = new ArrayList<>();
//            ResponseModel subjectsByMajor = restTemplate.getForObject(SUBJECT_URL+"findByMajorId/"+majorId,ResponseModel.class);
//            String jsonSubjectMajor = new ObjectMapper().writeValueAsString(subjectsByMajor.getData());
//            List<Subject> convertJsonSubject = new ObjectMapper().readValue(jsonSubjectMajor, new TypeReference<List<Subject>>() {
//            });
//            for(Subject subject : convertJsonSubject){
//                StudentSubject studentSubject = new StudentSubject(subject.getId(),studentResponse.getId(),"0");
//                studentSubjectList.add(studentSubject);
//            }
//            String studentSubjectListToJson = new ObjectMapper().writeValueAsString(studentSubjectList);
//            HttpHeaders headersStudentSubject = new HttpHeaders();
//            headersStudentSubject.set("Content-Type", "multipart/form-data");
//            headersStudentSubject.set("Authorization","Bearer "+_token);
//            MultiValueMap<String, String> paramsStudentSubject = new LinkedMultiValueMap<>();
//            paramsStudentSubject.add("student_subjectList",studentSubjectListToJson);
//            HttpEntity<MultiValueMap<String, String>> requestEntityStudentSubject = new HttpEntity<>(paramsStudentSubject,headersStudentSubject);
//            ResponseEntity<ResponseModel> responseModelStudentSubject= restTemplate.exchange(STUDENT_SUBJECT_URL, HttpMethod.POST,requestEntityStudentSubject,ResponseModel.class);
            //-------------------

            //save major-student
            MajorStudent majorStudent = new MajorStudent(majorId, studentResponse.getId());
            String jsonMajorStudent = new ObjectMapper().writeValueAsString(majorStudent);
            HttpHeaders headersMajorStudent = new HttpHeaders();
            headersMajorStudent.set("Content-Type", "multipart/form-data");
            headersMajorStudent.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, String> paramsMajorStudent = new LinkedMultiValueMap<>();
            paramsMajorStudent.add("student_major", jsonMajorStudent);
            HttpEntity<MultiValueMap<String, String>> requestEntityMajorStudent = new HttpEntity<>(paramsMajorStudent, headersMajorStudent);
            ResponseEntity<ResponseModel> responseStudentMajor = restTemplate.exchange(STUDENT_MAJOR_URL, HttpMethod.POST, requestEntityMajorStudent, ResponseModel.class);
            //---------
            log.info("FINISH create-student");
            return "success";
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }
        }
    }

    @GetMapping("/dashboard/student/index-student")
    public String index(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "list", HttpMethod.GET, request, String.class);
            List<Student> listStudent = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Student>>() {
            });
            List<Province> provinces = restTemplate.getForObject(PROVINCE_URL, ArrayList.class);
            model.addAttribute("provinces", provinces);
            model.addAttribute("students", listStudent.stream().sorted((s1, s2) -> s2.getId().compareTo(s1.getId())).toList());
            return "dashboard/student/student_index";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @GetMapping("/dashboard/student/student_details/{id}")
    @ResponseBody
    public Object student_details(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") Integer id) throws JsonProcessingException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "get/" + id, HttpMethod.GET, request, String.class);
            ResponseModel responseModel = objectMapper.readValue(response.getBody(), new TypeReference<ResponseModel>() {
            });
            String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
            Student student = objectMapper.readValue(convertToJson, Student.class);

            HttpHeaders headersStudentClass = new HttpHeaders();
            headersStudentClass.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> requestStudentClass = new HttpEntity<>(headersStudentClass);
            ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "getStudent/" + id, HttpMethod.GET, requestStudentClass, ResponseModel.class);
            String json = objectMapper.writeValueAsString(responseStudentClass.getBody().getData());
            List<StudentClass> studentClasses = objectMapper.readValue(json, new TypeReference<List<StudentClass>>() {
            });
            List<Classses> list = new ArrayList<>();

            HttpHeaders headersClass = new HttpHeaders();
            headersClass.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> requestClass = new HttpEntity<>(headersClass);
            for (StudentClass studentClass : studentClasses) {
                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + studentClass.getClassId(), HttpMethod.GET, requestClass, ResponseModel.class);
                String jsonClass = objectMapper.writeValueAsString(responseClass.getBody().getData());
                Classses classses = objectMapper.readValue(jsonClass, Classses.class);
                list.add(classses);
            }
            return new StudentClassModel(student, list);
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }

        }
    }

    @PostMapping("/dashboard/student/reset_password")
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
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }

        }
    }


    @PostMapping("/dashboard/student/student_update")
    @ResponseBody
    public String student_update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestParam("profile") String profile) throws JsonProcessingException {
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
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }

        }
    }

    @PostMapping("/dashboard/student/changeImg")
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
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }
        }
    }

    @GetMapping("/dashboard/student/export-excel")
    public void exportExcel(@CookieValue(name = "_token", defaultValue = "") String _token,
                            HttpServletResponse responses) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "list", HttpMethod.GET, request, String.class);
        List<Student> listStudent = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Student>>() {
        });

        ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL + "get", HttpMethod.GET, request, String.class);
        ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "get", HttpMethod.GET, request, ResponseModel.class);
        String json = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
        List<StudentClass> listStudentClass = new ObjectMapper().readValue(json, new TypeReference<List<StudentClass>>() {
        });
        List<Classses> listClass = new ObjectMapper().readValue(responseClass.getBody(), new TypeReference<List<Classses>>() {
        });
        Student student = new Student();

        List<StudentClassModel> studentClassModels = new ArrayList<>();


        for (Student s : listStudent) {
            List<Classses> classsesList = new ArrayList<>();
            for (StudentClass studentClass : listStudentClass) {
                if (s.getId().equals(studentClass.getStudentId())) {
                    for (Classses classses : listClass) {
                        if (classses.getId().equals(studentClass.getClassId())) {
                            classsesList.add(classses);
                        }
                    }
                }
            }
            StudentClassModel studentClassModel = new StudentClassModel();
            studentClassModel.setStudent(s);
            studentClassModel.setClasses(classsesList);
            studentClassModels.add(studentClassModel);
        }


        responses.setContentType("application/octet-stream");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Danh_sach_sinh_vien_" + currentDate + ".xlsx"; // file *.xlsx
        responses.setHeader(headerKey, headerValue);
        StudentExport generateFeedbackExcel = new StudentExport(studentClassModels);
        generateFeedbackExcel.generateExcelFile(responses);
    }

    @PostMapping("/dashboard/student/import-excel")
    @ResponseBody
    public Object importExcel(@CookieValue(name = "_token") String _token,
                              @RequestParam("file") MultipartFile file) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                if (!file.isEmpty()) {
                    if (FileUtils.getExtension(file.getOriginalFilename()).equals("xlsx")) {
                        listStudent = new ArrayList<>();
                        try {
                            workbook = new XSSFWorkbook(file.getInputStream());
                            sheet = workbook.getSheetAt(0);
                            RestTemplate restTemplate = new RestTemplate();
                            String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                            String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
                            String numbers = "1234567890";
                            String combinedChars = capitalCaseLetters + lowerCaseLetters + numbers;

                            HttpHeaders headers = new HttpHeaders();
                            headers.set("Authorization", "Bearer " + _token);
                            HttpEntity<Object> request = new HttpEntity<>(headers);

                            //Select role
                            HttpHeaders headersRole = new HttpHeaders();
                            headersRole.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            MultiValueMap<String, String> paramRole = new LinkedMultiValueMap<>();
                            String roleName = "STUDENT";
                            paramRole.add("role", roleName);
                            HttpEntity<MultiValueMap<String, String>> requestRole = new HttpEntity<>(paramRole, headersRole);
                            ResponseEntity<String> responseRole = restTemplate.exchange(ROLE_URL + "get", HttpMethod.POST, requestRole, String.class);
                            Role role = new ObjectMapper().readValue(responseRole.getBody(), Role.class);

//                            int excelLength = ExcelHelper.getNumberOfNonEmptyCells(sheet, 3);
//                            if (excelLength < 26) {
                            for (int rowIndex = 2; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                                XSSFRow row = sheet.getRow(rowIndex);
                                Cell date = row.getCell(4);

                                String first_name = ExcelHelper.getValue(row.getCell(1)).toString();
                                String last_name = ExcelHelper.getValue(row.getCell(2)).toString();
                                String email = ExcelHelper.getValue(row.getCell(3)).toString();
                                String dob = "";
                                if (date != null) {
                                    dob = date.getLocalDateTimeCellValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                }
                                String phone = ExcelHelper.getValue(row.getCell(5)).toString();
                                String identity_card = ExcelHelper.getValue(row.getCell(6)).toString();
                                String gender = ExcelHelper.getValue(row.getCell(7)).toString();
                                String course = ExcelHelper.getValue(row.getCell(8)).toString();

                                if (!first_name.isEmpty() && !last_name.isEmpty() && !email.isEmpty() && !dob.isEmpty() && !phone.isEmpty()
                                        && !identity_card.isEmpty() && !gender.isEmpty() && !course.isEmpty()) {

                                    // Get Major
                                    ResponseEntity<ResponseModel> responseMajor = restTemplate.exchange(MAJOR_URL + "findByMajorCode/" + course, HttpMethod.GET, request, ResponseModel.class);
                                    String majorJson = new ObjectMapper().writeValueAsString(responseMajor.getBody().getData());
                                    Major major = new ObjectMapper().readValue(majorJson, Major.class);

                                    // Get Profile


                                    if (major != null) {
                                        String password = RandomStringUtils.random(8, 0, combinedChars.length(), true, true, combinedChars.toCharArray());

                                        String studentCard = StringUtils.randomStudentCard(numbers);
                                        ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "findStudentCard/" + studentCard, HttpMethod.GET, request, String.class);
                                        if (response.getBody() != null) {
                                            studentCard = StringUtils.randomStudentCard(numbers);
                                        }


                                        //Save Account
                                        Account account = new Account(studentCard, password, role.getId());
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

                                        Profile parseProfile = new Profile();
                                        parseProfile.setDob(dob);
                                        parseProfile.setEmail(email);
                                        parseProfile.setPhone(phone);
                                        parseProfile.setIdentityCard(identity_card);
                                        parseProfile.setFirstName(first_name);
                                        parseProfile.setLastName(last_name);
                                        parseProfile.setSex(gender);
                                        File fileImage = ResourceUtils.getFile("classpath:static/img/avatar.png");
                                        FileInputStream inputStream = new FileInputStream(fileImage);
                                        MultipartFile multipartFile = new MockMultipartFile("file", fileImage.getName(), "image/png", IOUtil.toByteArray(inputStream));

                                        //save profile
                                        HttpHeaders headersProfile = new HttpHeaders();
                                        headersProfile.set("Content-Type", "multipart/form-data");
                                        headersProfile.set("Authorization", "Bearer " + _token);
                                        MultiValueMap<String, Object> paramsProfile = new LinkedMultiValueMap<>();
                                        parseProfile.setAccountId(accountResponse.getId());
                                        String jsonProfile = new ObjectMapper().writeValueAsString(parseProfile);
                                        paramsProfile.add("file", multipartFile.getResource());
                                        paramsProfile.add("profile", jsonProfile);
                                        HttpEntity<MultiValueMap<String, Object>> requestEntityProfile = new HttpEntity<>(paramsProfile, headersProfile);
                                        ResponseEntity<ResponseModel> responseProfile = restTemplate.exchange(PROFILE_URL, HttpMethod.POST, requestEntityProfile, ResponseModel.class);
                                        String profileResponseToJson = new ObjectMapper().writeValueAsString(responseProfile.getBody().getData());
                                        Profile profileResponse = new ObjectMapper().readValue(profileResponseToJson, Profile.class);

//                                      //Send mail
                                        Mail mail = new Mail();
                                        mail.setToMail(parseProfile.getEmail());
                                        mail.setSubject("Account student HKT SYSTEM");
                                        String name = profileResponse.getFirstName() + " " + profileResponse.getLastName();
                                        Map<String, Object> props = new HashMap<>();
                                        props.put("accountName", studentCard);
                                        props.put("password", password);
                                        props.put("fullname", name);
                                        mail.setProps(props);
                                        mailService.sendHtmlMessage(mail);
                                        //-----------------------------

                                        //Save student
                                        //generate studentCard
                                        MultiValueMap<String, String> paramsStudent = new LinkedMultiValueMap<>();
                                        paramsStudent.add("studentCard", studentCard);
                                        paramsStudent.add("profileId", String.valueOf(profileResponse.getId()));
                                        HttpEntity<MultiValueMap<String, String>> requestEntityStudent = new HttpEntity<>(paramsStudent, headers);
                                        ResponseEntity<ResponseModel> responseModelStudent = restTemplate.exchange(STUDENT_URL, HttpMethod.POST, requestEntityStudent, ResponseModel.class);
                                        String studentResponseToJson = new ObjectMapper().writeValueAsString(responseModelStudent.getBody().getData());
                                        Student studentResponse = new ObjectMapper().readValue(studentResponseToJson, Student.class);
                                        //----------------------

                                        //save major-student
                                        MajorStudent majorStudent = new MajorStudent(major.getId(), studentResponse.getId());
                                        String jsonMajorStudent = new ObjectMapper().writeValueAsString(majorStudent);
                                        HttpHeaders headersMajorStudent = new HttpHeaders();
                                        headersMajorStudent.set("Content-Type", "multipart/form-data");
                                        headersMajorStudent.set("Authorization", "Bearer " + _token);
                                        MultiValueMap<String, String> paramsMajorStudent = new LinkedMultiValueMap<>();
                                        paramsMajorStudent.add("student_major", jsonMajorStudent);
                                        HttpEntity<MultiValueMap<String, String>> requestEntityMajorStudent = new HttpEntity<>(paramsMajorStudent, headersMajorStudent);
                                        ResponseEntity<ResponseModel> responseStudentMajor = restTemplate.exchange(STUDENT_MAJOR_URL, HttpMethod.POST, requestEntityMajorStudent, ResponseModel.class);
                                        //---------
                                    } else {
                                        return new ResponseEntity<String>("Can't find curriculum at row " + (rowIndex + 1), HttpStatus.BAD_REQUEST);
                                    }
                                } else {
                                    return new ResponseEntity<String>("Some field at row " + (rowIndex + 1) + " is empty please fill it", HttpStatus.BAD_REQUEST);
                                }
                            }
                            return new ResponseEntity<String>("Success", HttpStatus.OK);
//                            } else {
//                                return new ResponseEntity<String>("Excel data must letter than 26 student", HttpStatus.BAD_REQUEST);
//                            }
                        } catch (Exception e) {
                            log.error("Import Student: " + e.getMessage());
                            return new ResponseEntity<String>("Import excel fail", HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        return new ResponseEntity<String>("Please choose excel file", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<String>("Please choose file", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Import excel fail", HttpStatus.NOT_FOUND);
        }
    }
}
