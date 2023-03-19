package com.example.smsweb.client.teacher;

import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.teacher.MarkList;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.ExcelHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.math.raw.Mod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/teacher")
public class HomeController {
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
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

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = {"/index", ""})
    public String index(Model model,
                        @CookieValue("_token")String _token,
                        Principal principal) {
        try{
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            //Get class
            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "list", HttpMethod.GET, request, String.class);
            List<Classses> classList = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            classList = classList.stream()
                    .filter(p -> p.getTeacher().getProfileByProfileId().getAccountByAccountId().getUsername().equals(principal.getName()))
                    .sorted(Comparator.comparingInt(Classses::getId))
                    .collect(Collectors.toList());

            //Get total students
            List<StudentClass> studentClassList = new ArrayList<>();
            for (Classses classs : classList) {
                studentClassList.addAll(classs.getStudentClassById());
            }
            int totalStudent = studentClassList.stream()
                    .filter(distinctByKey(StudentClass::getStudentId)).collect(Collectors.toList()).size();

            //Get scheduleList
            List<Schedule> scheduleList = new ArrayList<>();
            List<ScheduleDetail> scheduleDetailList = new ArrayList<>();

            SimpleDateFormat formDate = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formDate.format(new Date());

            classList.forEach(classs -> {
                try {
                    //get schedule add to scheduleList
                    content.add("classId",classs.getId());
                    HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(content, headers);
                    ResponseEntity<String> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClass",
                            HttpMethod.POST, requestSchedule, String.class);
                    ResponseModel responseModelSchedule = objectMapper.readValue(responseSchedule.getBody(), new TypeReference<>() {});
                    String scheduleJson = objectMapper.writeValueAsString(responseModelSchedule.getData());
                    List<Schedule> schedule = objectMapper.readValue(scheduleJson, new TypeReference<>() {});
                    scheduleList.addAll(schedule);

                    //get scheduleDetailList
//                    content.add("date",strDate);
//                    content.add("scheduleId",schedule.getId());
//                    HttpEntity<MultiValueMap<String, Object>> requestSheduleDetail = new HttpEntity<>(content, headers);
//                    ResponseEntity<ResponseModel> responseSheduleDetail = restTemplate.exchange(
//                            SCHEDULE_DETAIL_URL+"", HttpMethod.POST,requestSheduleDetail, ResponseModel.class);
//                    String scheduleDetailJson = objectMapper.writeValueAsString(responseSheduleDetail.getBody().getData());
//                    ScheduleDetail scheduleDetail = objectMapper.readValue(scheduleDetailJson, new TypeReference<ScheduleDetail>() {});
                }catch (Exception e){
                    throw new ErrorHandler(e.getMessage());
                }

            });


            //Add attribute
            model.addAttribute("totalClass", classList.size());
            model.addAttribute("totalStudent", totalStudent);

            return "teacherDashboard/home";
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
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
            List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {});
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
                    .filter(distinctByKey(Student::getId))
                    .collect(Collectors.toList());

            model.addAttribute("students",collect);

            return "teacherDashboard/student/student_index";
        }catch (Exception ex){
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
            List<News> newsList = new ObjectMapper().readValue(json, new TypeReference<List<News>>() {});
            model.addAttribute("news", newsList);
            return "teacherDashboard/news/new_index";
        }catch (Exception ex){
            if (ex.getMessage().equalsIgnoreCase("Token expired")){
                return "redirect:/login";
            }
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @GetMapping("/classes")
    public String classes(Model model,
                          @CookieValue(name = "_token", defaultValue = "") String _token,
                          Principal principal) {
        try{
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
            JWTUtils.checkExpired(_token);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classCode", classCode);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST, request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classModel = new ObjectMapper().readValue(json, Classses.class);
            if (classModel.getTeacher()
                    .getProfileByProfileId()
                    .getAccountByAccountId()
                    .getUsername()
                    .equals(principal.getName())){
                model.addAttribute("class", classModel);
                return "teacherDashboard/class/class_details";
            }else {
                model.addAttribute("msg", "You have no permission");
                return "redirect:/teacher/classes";
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/teacherDashboard/logout";
        }
    }

    @PostMapping("class/import-mark-list")
    @ResponseBody
    public String importScoreList(Model model,
                                  @CookieValue(name = "_token", defaultValue = "")String _token,
                                  @RequestParam("teacherId")int teacherId,
                                  @RequestParam("classId")int classId,
                                  @RequestParam(name = "mark_list", required = false) MultipartFile file) {
        try{
            JWTUtils.checkExpired(_token);

            if (file != null){
                List<MarkList> inputMarkList = new ArrayList<>();
                List<Student> studentList = new ArrayList<>();
                List<StudentSubject> studentSubjectList = new ArrayList<>();
                List<Mark> markList = new ArrayList<>();

                //get excel data to markList
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    if (rowIndex < 2) {
                        continue;
                    }
                    String studentCode = ExcelHelper.getValue(row.getCell(1)).toString();
                    String majorCode = ExcelHelper.getValue(row.getCell(2)).toString();
                    String asmMark = ExcelHelper.getValue(row.getCell(3)).toString();
                    String objMark = ExcelHelper.getValue(row.getCell(4)).toString();

                    inputMarkList.add(new MarkList(studentCode, majorCode, Double.parseDouble(asmMark), Double.parseDouble(objMark)));
                }
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<Object> request = new HttpEntity<>(headers);
                MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
                ObjectMapper objectMapper = new ObjectMapper();

                //Get class
                ResponseEntity<String> responseClass = restTemplate.exchange(
                        CLASS_URL + "getClass/" + classId,
                        HttpMethod.GET, request, String.class);
                ResponseModel responseModelClass  = objectMapper.readValue(responseClass.getBody(), new TypeReference<>() {});
                String classJson = objectMapper.writeValueAsString(responseModelClass.getData());
                Classses classs = objectMapper.readValue(classJson, new TypeReference<>() {});


                //Check valid data and validate exception
                for (MarkList inputMark : inputMarkList) {
                    try {
                        //Check student, check student in class and add to studentList
                        ResponseEntity<String> responseStudent = restTemplate.exchange(
                                STUDENT_URL + "findStudentByStudentCard/" + inputMark.getStudentCode(),
                                HttpMethod.GET, request, String.class);
                        Student student = objectMapper.readValue(responseStudent.getBody(), new TypeReference<>() {});

                        ResponseEntity<String> responseStudentClass = restTemplate.exchange(
                                STUDENT_CLASS_URL + "findStudentClassesByStudentId/" + student.getId(),
                                HttpMethod.GET, request, String.class);
                        ResponseModel responseModelStudentClass  = objectMapper.readValue(responseStudentClass.getBody(), new TypeReference<>() {});
                        String studentClassJson = objectMapper.writeValueAsString(responseModelStudentClass.getData());
                        List<StudentClass> studentClassList = objectMapper.readValue(studentClassJson, new TypeReference<>() {});

                        if (studentClassList.stream().filter(studentClass -> studentClass.getClassId()==classId).collect(Collectors.toList()).size() == 0){
                            throw new ErrorHandler("You have no permission to mark this student: "+ student.getStudentCard());
                        };
                        studentList.add(student);

                        //Check existed subject and subject existed in this class
                        ResponseEntity<String> responseSubject = restTemplate.exchange(
                                SUBJECT_URL + "findSubjectBySubjectCode/" + inputMark.getSubjectCode(),
                                HttpMethod.GET, request, String.class);
                        ResponseModel responseModelMajor = objectMapper.readValue(responseSubject.getBody(), new TypeReference<>() {
                        });
                        String majorJson = objectMapper.writeValueAsString(responseModelMajor.getData());
                        Subject subject = objectMapper.readValue(majorJson, Subject.class);
                        if (classs.getMajor().getSubjectsById().stream().filter(s -> s.getId() == subject.getId()).collect(Collectors.toList()).size() == 0){
                            throw new ErrorHandler("You have no permission to mark this subject: "+subject.getSubjectCode());
                        }


                        //Check existed StudentSubject add to studentSubjectList
                        content.add("studentId", student.getId());
                        content.add("subjectId", subject.getId());
                        HttpEntity<MultiValueMap<String, Object>> requestStudentSubject = new HttpEntity<>(content, headers);
                        ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "findStudentSubjectBySubjectIdAndStudentId",
                                HttpMethod.POST, requestStudentSubject, ResponseModel.class);
                        content.remove("studentId");
                        content.remove("subjectId");

                        String studentSubjectJson = objectMapper.writeValueAsString(responseStudentSubject.getBody().getData());
                        if(studentSubjectJson == null || studentSubjectJson == ""){
                            throw new ErrorHandler("Student "+ student.getStudentCard() +" have not learned "+ subject.getSubjectCode()+" yet !");
                        }
                        StudentSubject studentSubject = objectMapper.readValue(studentSubjectJson, StudentSubject.class);
                        studentSubjectList.add(studentSubject);

                        //field studentId, subjectId to markList
                        inputMark.setStudentCode(student.getId().toString());
                        inputMark.setSubjectCode(String.valueOf(subject.getId()));

                        //Add markList
                        MarkList first = inputMarkList.stream()
                                .filter(p -> p.getSubjectCode().equals(studentSubject.getSubjectId().toString()))
                                .filter(p -> p.getStudentCode().equals(studentSubject.getStudentId().toString()))
                                .findFirst()
                                .orElseThrow(()-> new ErrorHandler("Student "+ student.getStudentCard() +" have not learned "+ subject.getSubjectCode()+" yet !"));

                        Mark mark = new Mark(0, first.getAsmMark(), first.getObjMark(), studentSubject.getId(), null);
                        //Check existed mark in database
                        ResponseEntity<Mark> responseMark = restTemplate.exchange(
                                MARK_URL + "findMarkByStudentSubjectId/" + mark.getStudentSubjectId(),
                                HttpMethod.GET, request, Mark.class);
                        Mark body = responseMark.getBody();
                        if(body != null){
                            throw new ErrorHandler(student.getStudentCard() +" already have a record of mark in "+ subject.getSubjectCode()+"!");
                            //mark.setId(body.getId());
                        }
                        markList.add(mark);

                    } catch (Exception e) {
                        throw new ErrorHandler(e.getMessage());
                    }
                }


                //Save markList
                String markListJson = objectMapper.writeValueAsString(markList);
                content.add("markList", markListJson);
                HttpEntity<MultiValueMap<String, Object>> requestMark = new HttpEntity<>(content, headers);
                ResponseEntity<ResponseModel> responseMark = restTemplate.exchange(MARK_URL + "saveAll",
                        HttpMethod.POST, requestMark, ResponseModel.class);
                content.remove("markList");
                if (responseMark.getStatusCode().is2xxSuccessful()){
                    return "success";
                }else {
                    throw new ErrorHandler("Save mark list failed");
                }
            }else {
                throw new ErrorHandler("Empty list");
            }
        } catch (Exception e) {
            throw  new ErrorHandler(e.getMessage());
        }
    }

    @PostMapping("class/update-mark-list")
    @ResponseBody
    public String updateScoreList(Model model,
                                  @CookieValue(name = "_token", defaultValue = "")String _token,
                                  @RequestParam("teacherId")int teacherId,
                                  @RequestParam("classId")int classId,
                                  @RequestParam(name = "mark_list", required = false) MultipartFile file) {
        try{
            JWTUtils.checkExpired(_token);

            if (file != null){
                List<MarkList> inputMarkList = new ArrayList<>();
                List<Student> studentList = new ArrayList<>();
                List<StudentSubject> studentSubjectList = new ArrayList<>();
                List<Mark> markList = new ArrayList<>();

                //get excel data to markList
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    if (rowIndex < 2) {
                        continue;
                    }
                    String studentCode = ExcelHelper.getValue(row.getCell(1)).toString();
                    String majorCode = ExcelHelper.getValue(row.getCell(2)).toString();
                    String asmMark = ExcelHelper.getValue(row.getCell(3)).toString();
                    String objMark = ExcelHelper.getValue(row.getCell(4)).toString();

                    inputMarkList.add(new MarkList(studentCode, majorCode, Double.parseDouble(asmMark), Double.parseDouble(objMark)));
                }
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<Object> request = new HttpEntity<>(headers);
                MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
                ObjectMapper objectMapper = new ObjectMapper();

                //Get class
                ResponseEntity<String> responseClass = restTemplate.exchange(
                        CLASS_URL + "getClass/" + classId,
                        HttpMethod.GET, request, String.class);
                ResponseModel responseModelClass  = objectMapper.readValue(responseClass.getBody(), new TypeReference<>() {});
                String classJson = objectMapper.writeValueAsString(responseModelClass.getData());
                Classses classs = objectMapper.readValue(classJson, new TypeReference<>() {});


                //Check valid data and validate exception
                for (MarkList inputMark : inputMarkList) {
                    try {
                        //Check student, check student in class and add to studentList
                        ResponseEntity<String> responseStudent = restTemplate.exchange(
                                STUDENT_URL + "findStudentByStudentCard/" + inputMark.getStudentCode(),
                                HttpMethod.GET, request, String.class);
                        Student student = objectMapper.readValue(responseStudent.getBody(), new TypeReference<>() {});

                        ResponseEntity<String> responseStudentClass = restTemplate.exchange(
                                STUDENT_CLASS_URL + "findStudentClassesByStudentId/" + student.getId(),
                                HttpMethod.GET, request, String.class);
                        ResponseModel responseModelStudentClass  = objectMapper.readValue(responseStudentClass.getBody(), new TypeReference<>() {});
                        String studentClassJson = objectMapper.writeValueAsString(responseModelStudentClass.getData());
                        List<StudentClass> studentClassList = objectMapper.readValue(studentClassJson, new TypeReference<>() {});

                        if (studentClassList.stream().filter(studentClass -> studentClass.getClassId()==classId).collect(Collectors.toList()).size() == 0){
                            throw new ErrorHandler("You have no permission to mark this student: "+ student.getStudentCard());
                        };
                        studentList.add(student);

                        //Check existed subject and subject existed in this class
                        ResponseEntity<String> responseSubject = restTemplate.exchange(
                                SUBJECT_URL + "findSubjectBySubjectCode/" + inputMark.getSubjectCode(),
                                HttpMethod.GET, request, String.class);
                        ResponseModel responseModelMajor = objectMapper.readValue(responseSubject.getBody(), new TypeReference<>() {
                        });
                        String majorJson = objectMapper.writeValueAsString(responseModelMajor.getData());
                        Subject subject = objectMapper.readValue(majorJson, Subject.class);
                        if (classs.getMajor().getSubjectsById().stream().filter(s -> s.getId() == subject.getId()).collect(Collectors.toList()).size() == 0){
                            throw new ErrorHandler("You have no permission to mark this subject: "+subject.getSubjectCode());
                        }


                        //Check existed StudentSubject add to studentSubjectList
                        content.add("studentId", student.getId());
                        content.add("subjectId", subject.getId());
                        HttpEntity<MultiValueMap<String, Object>> requestStudentSubject = new HttpEntity<>(content, headers);
                        ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "findStudentSubjectBySubjectIdAndStudentId",
                                HttpMethod.POST, requestStudentSubject, ResponseModel.class);
                        content.remove("studentId");
                        content.remove("subjectId");

                        String studentSubjectJson = objectMapper.writeValueAsString(responseStudentSubject.getBody().getData());
                        if(studentSubjectJson == null || studentSubjectJson == ""){
                            throw new ErrorHandler("Student "+ student.getStudentCard() +" have not learned "+ subject.getSubjectCode()+" yet !");
                        }
                        StudentSubject studentSubject = objectMapper.readValue(studentSubjectJson, StudentSubject.class);
                        studentSubjectList.add(studentSubject);

                        //field studentId, subjectId to markList
                        inputMark.setStudentCode(student.getId().toString());
                        inputMark.setSubjectCode(String.valueOf(subject.getId()));

                        //Add markList
                        MarkList first = inputMarkList.stream()
                                .filter(p -> p.getSubjectCode().equals(studentSubject.getSubjectId().toString()))
                                .filter(p -> p.getStudentCode().equals(studentSubject.getStudentId().toString()))
                                .findFirst()
                                .orElseThrow(()-> new ErrorHandler("Student "+ student.getStudentCard() +" have not learned "+ subject.getSubjectCode()+" yet !"));

                        Mark mark = new Mark(0, first.getAsmMark(), first.getObjMark(), studentSubject.getId(), null);
                        //Check existed mark in database
                        ResponseEntity<Mark> responseMark = restTemplate.exchange(
                                MARK_URL + "findMarkByStudentSubjectId/" + mark.getStudentSubjectId(),
                                HttpMethod.GET, request, Mark.class);
                        Mark body = responseMark.getBody();
                        if(body == null){
                            throw new ErrorHandler(student.getStudentCard() +" dont have record of mark in "+ subject.getSubjectCode()+"!");
                        }
                        mark.setId(body.getId());
                        markList.add(mark);

                    } catch (Exception e) {
                        throw new ErrorHandler(e.getMessage());
                    }
                }


                //Save markList
                String markListJson = objectMapper.writeValueAsString(markList);
                content.add("markList", markListJson);
                HttpEntity<MultiValueMap<String, Object>> requestMark = new HttpEntity<>(content, headers);
                ResponseEntity<ResponseModel> responseMark = restTemplate.exchange(MARK_URL + "saveAll",
                        HttpMethod.POST, requestMark, ResponseModel.class);
                content.remove("markList");
                if (responseMark.getStatusCode().is2xxSuccessful()){
                    return "success";
                }else {
                    throw new ErrorHandler("Update mark list failed");
                }
            }else {
                throw new ErrorHandler("Empty list");
            }
        } catch (Exception e) {
            throw  new ErrorHandler(e.getMessage());
        }
    }
}
