package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Department;
import com.example.smsweb.models.Major;
import com.example.smsweb.models.Province;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Controller
@RequestMapping("dashboard/department")
@Slf4j
public class DepartmentController {

    private final String URL_DEPARTMENT = "http://localhost:8080/api/department/";

    @GetMapping("/index")
    public String index(Model model, @CookieValue(name = "_token", defaultValue = "") String _token){
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseModel response = restTemplate.getForObject(URL_DEPARTMENT,ResponseModel.class);
            String json =objectMapper.writeValueAsString(response.getData());
            List<Department> departmentList = objectMapper.readValue(json, new TypeReference<List<Department>>() {
            });
            model.addAttribute("departmentList",departmentList);
            return "dashboard/department/department_index";
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @PostMapping("/create_department")
    @ResponseBody
    public Object create_department(@CookieValue(name = "_token", defaultValue = "") String _token,
    @RequestParam("department")String departmentJSON) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            params.add("departmentJSON",departmentJSON);
            ResponseModel response = restTemplate.postForObject(URL_DEPARTMENT,params,ResponseModel.class);
            return "success";
        }catch (HttpClientErrorException ex){
            log.error(ex.getMessage());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                return ex.getMessage();
            }else {
                return ex.getMessage();
            }
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token,
                                    @RequestParam("department")String departmentJSON) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            params.add("departmentJSON",departmentJSON);
            restTemplate.put(URL_DEPARTMENT,params,ResponseModel.class);
            return "success";
        }catch (HttpClientErrorException ex){
            log.error(ex.getMessage());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                return ex.getMessage();
            }else {
                return ex.getMessage();
            }
        }
    }

    @GetMapping("/find/{id}")
    @ResponseBody
    public Object find(@CookieValue(name = "_token", defaultValue = "") String _token,
                                    @PathVariable("id")Integer id) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseModel response = restTemplate.getForObject(URL_DEPARTMENT+id,ResponseModel.class);
            return response.getData();
        }catch (HttpClientErrorException ex){
            log.error(ex.getMessage());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                return ex.getMessage();
            }else {
                return ex.getMessage();
            }
        }
    }
}
