package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.di.irepository.IClass;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.ExcelHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Classes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/dashboard/class")
public class ClassController {

    private String MAJOR_URL = "http://localhost:8080/api/major/";
    private String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";


    @GetMapping("/class-index")
    public String index(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"list",HttpMethod.GET,request,String.class);
        List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Classses>>(){});


        model.addAttribute("classes",classList);
        return "dashboard/class/class_index";
    }


    @GetMapping("/class-create")
    public String createClass(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> teacherResponse = restTemplate.exchange(TEACHER_URL+"list",HttpMethod.GET,request,String.class);
        List<Teacher> teacherList = new ObjectMapper().readValue(teacherResponse.getBody(), new TypeReference<List<Teacher>>(){});

        ResponseModel listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);

        model.addAttribute("majors", listMajor.getData());
        model.addAttribute("teachers",teacherList);
        return "dashboard/class/class_create";
    }

    @PostMapping("/class-create")
    @ResponseBody
    public String createClass(Model model,
                                @CookieValue(name = "_token", defaultValue = "") String _token,
                                @RequestParam("newClass")String newClass,
                                @RequestParam(name = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {
        try{
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","Bearer "+_token);

            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            content.add("newClass", newClass);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Classses classModel = objectMapper.readValue(newClass, Classses.class);
                Object data = response.getBody().getData();
                if(file != null){
                    try{
                        Boolean addStudent = UpdateClassIdForStudentClass(_token, file, classModel.getClassCode());
                        if (addStudent){
                            return new responModelForClass("success","Thêm sinh viên thành công").toString();
                        }else {
                            return new responModelForClass("success","Thêm sinh viên thất bại").toString();
                        }
                    } catch (Exception e) {
                        String message = StringUtils.substringBetween(e.getMessage(), "\"", "\"");
                        return new responModelForClass("success","Can not import student. "+message).toString();
                    }
                }else {
                    return new responModelForClass("success","").toString();
                }
            } else {
                return new responModelForClass("fail","").toString();
            }
        } catch (HttpClientErrorException ex){
            log.error(ex.getMessage());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                return ex.getMessage();
            }else {
                return ex.getMessage();
            }

        }

    }

    private Boolean UpdateClassIdForStudentClass(String _token, MultipartFile file, String classCode) throws JsonProcessingException {
        if (!file.isEmpty()) {
            try {
                // get list studentId form Excel file
                List<String> listStudentCard = new ArrayList<>();
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    if (rowIndex == 0) {
                        continue;
                    }
                    String student_id = ExcelHelper.getValue(row.getCell(0)).toString();
                    if (!student_id.isEmpty() && listStudentCard.indexOf(student_id) == -1) {
                        listStudentCard.add(student_id);
                    }
                }

                if (!listStudentCard.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers= new HttpHeaders();
                    headers.set("Authorization","Bearer "+_token);

                    MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);

                    ResponseEntity<String> responseStudent = restTemplate.exchange(STUDENT_URL+"findStudentIdByRangeStudentCard/"+new ObjectMapper().writeValueAsString(listStudentCard),
                            HttpMethod.GET,request,String.class);
                    List<Student> listStudent = objectMapper.readValue(responseStudent.getBody(), new TypeReference<List<Student>>(){});

                    content.add("classCode", classCode);
                    request = new HttpEntity<>(content, headers);
                    ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL+"findClassCode", HttpMethod.POST, request, String.class);
                    content.remove("classCode");
                    ResponseModel responseModel = objectMapper.readValue(responseClass.getBody(),new TypeReference<ResponseModel>(){});
                    String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
                    Classses classModel = objectMapper.readValue(convertToJson,Classses.class);
                    List<StudentClass> studentClassList = new ArrayList<>();
                    for (Student student :listStudent) {
                        StudentClass studentClass = new StudentClass();
                        studentClass.setStudentId(student.getId());
                        studentClass.setClassId(classModel.getId());
                        studentClassList.add(studentClass);
                    }
                    content.add("listStudentClass",objectMapper.writeValueAsString(studentClassList));
                    request = new HttpEntity<>(content, headers);
                    ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL+"saveAll", HttpMethod.POST, request, ResponseModel.class);

                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                var  s = e.getMessage();
                throw new RuntimeException(e.getMessage());
            }
        }
        return false;
    }

    @GetMapping("/class-details/{id}")
    @ResponseBody
    public Object class_details(@CookieValue(name = "_token", defaultValue = "") String _token,
                                  @PathVariable("id")Integer id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"findOne/"+id,
                HttpMethod.GET,request,String.class);
        ResponseModel responseModel = objectMapper.readValue(response.getBody(),new TypeReference<ResponseModel>(){});
        String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
        Classses classModel = objectMapper.readValue(convertToJson,Classses.class);
        return classModel;
    }

    @PostMapping("/class-checkExisted")
    @ResponseBody
    public String checkExistedClass(@CookieValue(name = "_token", defaultValue = "") String _token,
                                    @RequestParam("classCode")String classCode) throws JsonProcessingException {
        try{
            JWTUtils.checkExpired(_token);
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers= new HttpHeaders();
            headers.set("Authorization","Bearer "+_token);

            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            content.add("classCode", classCode);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"findClassCode", HttpMethod.POST, request, String.class);

            return response.toString();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                return e.getMessage();
            }else return e.getMessage();
        }
    }


    @GetMapping("/class-searchClasssesByClassCode")
    @ResponseBody
    public String searchClasssesByClassCode(Model model,@CookieValue(name = "_token", defaultValue = "") String _token,
                                                   @RequestParam("classCode")String classCode) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"searchClasssesByClassCode/"+classCode,HttpMethod.GET,request,String.class);
        return response.getBody();
    }

    class responModelForClass{
        String status;
        String message;

        public responModelForClass(String status, String message) {
            this.status = status;
            this.message = message;
        }

        @Override
        public String toString() {
            return "{\"status\":\""+status+"\"," +
                    "\"message\":\""+message+"\"" +
                    "}";
        }
    }
}
