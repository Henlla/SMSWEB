package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.ApplicationType;
import com.example.smsweb.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("dashboard/applicationType")
@Slf4j
public class ApplicationTypeController {
    private final String APPLICATION_TYPE_URL = "http://localhost:8080/api/application_type/";
    private RestTemplate restTemplate;
    private final String APP_TYPE_STORE_URL = "/src/main/resources/static/application/ApplicationTemplate/";

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if(!isExpired.toLowerCase().equals("token expired")){
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_TYPE_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                model.addAttribute("listAppType", response.getBody().getData());
                return "dashboard/application/app_type_index";
            }else{
                return "redirect:/dashboard/login";
            }
        } catch (Exception e) {
            log.error("Index ApplicationType: " + e.getMessage());
            return e.getMessage();
        }
    }

    @PostMapping("/save_app_type")
    @ResponseBody
    public Object save(@CookieValue(name = "_token", defaultValue = "") String _token,
                       @RequestParam("file") MultipartFile file, @RequestParam("name") String name,
                       @RequestParam("base64String") String base64) throws JsonProcessingException {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if(!isExpired.toLowerCase().equals("token expired")){
                String extension = FileNameUtils.getExtension(file.getOriginalFilename());
                restTemplate = new RestTemplate();
                String fileName = name + "." + extension;
                String url = "/application/ApplicationTemplate/" + fileName;
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
                ApplicationType appType = ApplicationType.builder().url(url).name(fileName).file(base64).build();
                content.add("applicationType", appType);
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(content, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_TYPE_URL + "save", HttpMethod.POST, request, ResponseModel.class);
                FileUtils.uploadFile(fileName, APP_TYPE_STORE_URL, file);
                return response.getBody().getData();
            }else{
                return new ResponseEntity<String>(isExpired,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Save ApplicationType: " + e.getMessage());
            return new ResponseEntity<String>("Tạo mới thất bại",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/deleteAppType/{id}")
    @ResponseBody
    public Object delete(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) throws JsonProcessingException {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if(!isExpired.toLowerCase().equals("token expired")){
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer" + _token);
                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<ResponseModel> responseFindOne = restTemplate.exchange(APPLICATION_TYPE_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
                ResponseEntity<ResponseModel> responseDelete = restTemplate.exchange(APPLICATION_TYPE_URL + "delete/" + id, HttpMethod.DELETE, request, ResponseModel.class);
                String jsonResponse = new ObjectMapper().writeValueAsString(responseFindOne.getBody().getData());
                ApplicationType appType = new ObjectMapper().readValue(jsonResponse, new TypeReference<>() {});
                String rootPath = System.getProperty("user.dir");
                File uploadDir = new File(rootPath + APP_TYPE_STORE_URL + appType.getName());
                if (uploadDir.exists()) {
                    uploadDir.delete();
                }
                return responseDelete.getBody().getData();
            }else{
                return new ResponseEntity<String>(isExpired,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Delete ApplicationType: " + e.getMessage());
            return new ResponseEntity<String>("Xóa thất bại",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get_one_app_type/{id}")
    @ResponseBody
    public Object getOneAppType(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") String id) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if(!isExpired.toLowerCase().equals("token expired")){
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_TYPE_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
                return response.getBody().getData();
            }else{
                return new ResponseEntity<String>(isExpired,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("GetOne ApplicationType: " + e.getMessage());
            return new ResponseEntity<String>("Không tìm thấy dữ liệu",HttpStatus.NOT_FOUND);
        }
    }
}
