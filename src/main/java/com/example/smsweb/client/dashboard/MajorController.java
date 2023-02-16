package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Major;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.List;

@Controller
@MultipartConfig
@RequestMapping("dashboard/major")
public class MajorController {
    @Autowired
    private IMajor service;
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    ResponseModel listMajor;
    RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            listMajor = new ResponseModel();
            HttpHeaders headers = new HttpHeaders();
            listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
            model.addAttribute("listMajor", listMajor.getData());
            model.addAttribute("major", new Major());
            return "dashboard/major/major_index";
        } catch (Exception e) {
            return "redirect:/dashboard/login";
        }
    }

    @PostMapping("/save")
    @ResponseBody
    public Object post(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Major major) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("majorCode", major.getMajorCode());
            content.add("majorName", major.getMajorName());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Object findOne(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
            return response.getBody().getData();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Major major) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("id", String.valueOf(major.getId()));
            content.add("majorCode", major.getMajorCode());
            content.add("majorName", major.getMajorName());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("id", String.valueOf(id));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            ResponseEntity<String> response = restTemplate.exchange(MAJOR_URL + "delete/" + id, HttpMethod.DELETE, request, String.class);
            return "redirect:/dashboard/major/index";
        } catch (Exception e) {
            return "redirect:/dashboard/login";
        }
    }

    @GetMapping("/export-excel")
    public String exportExcel(@CookieValue(name = "_token", defaultValue = "") String _token, HttpServletResponse responses) throws IOException {
        ResponseEntity<ResponseModel> response = null;
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Object> request = new HttpEntity<>(headers);
            response = restTemplate.exchange(MAJOR_URL + "list", HttpMethod.GET, request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            List<Major> listMajors = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
            service.exportDataToExcel(responses, listMajors, "major_export");
            return "Xuất dữ liệu thành công";
        } catch (Exception e) {
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return response.getBody().getData().toString();
            } else {
                return e.getMessage();
            }
        }
    }

    @PostMapping(value = "/import-excel")
    @ResponseBody
    public void importExcel(@RequestParam("file") MultipartFile file) {
        service.importDataToDb(file);
    }
}
