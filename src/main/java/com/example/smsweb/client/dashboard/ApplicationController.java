package com.example.smsweb.client.dashboard;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.ApplicationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("dashboard/application")
@MultipartConfig
public class ApplicationController {
    private final String APPLICATION_URL = "http://localhost:8080/api/application/";
    private final String APPLICATION_TYPE_URL = "http://localhost:8080/api/application_type/";
    private RestTemplate restTemplate;
    private final String APP_TYPE_STORE_URL = "/src/main/resources/static/application/ApplicationTemplate/";
    private ResponseModel application;

    @GetMapping("/app_type_index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_TYPE_URL + "list", HttpMethod.GET, request, ResponseModel.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("listAppType", response.getBody().getData());
        }
        return "dashboard/application/app_type_index";
    }

    @GetMapping("/app_index")
    public String regis_index(@CookieValue(name = "_token",defaultValue = "") String _token, Model model) {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<ResponseModel> responseApplication = restTemplate.exchange(APPLICATION_URL + "list", HttpMethod.GET, request, ResponseModel.class);
        ResponseEntity<ResponseModel> responseAppType = restTemplate.exchange(APPLICATION_TYPE_URL +"list",HttpMethod.GET,request,ResponseModel.class);
        if (responseApplication.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("listApplication", responseApplication.getBody().getData());
            model.addAttribute("listAppType",responseAppType.getBody().getData());
        }
        return "dashboard/application/app_index";
    }

    @PostMapping("/save_app_type")
    @ResponseBody
    public Object save(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestParam("file") MultipartFile file, @RequestParam("name") String name) throws JsonProcessingException {
        if(_token.equals("")){
            return "Token Expired";
        }
        DecodedJWT jwt = JWT.decode(_token);
        if(jwt.getExpiresAt().before(new Date())) {
            return "fail";
        }
        String extension = FileNameUtils.getExtension(file.getOriginalFilename());
        restTemplate = new RestTemplate();
        if (extension.equals("docx")) {
            String fileName = name + "." + extension;
            String url = "/application/ApplicationTemplate/" + fileName;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","Bearer " + _token);
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("name", name);
            content.add("url",url);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_TYPE_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                uploadFile(fileName, file);
                return response;
            } else {
                return response;
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Error", LocalDate.now().toString(), "Vui lòng chọn file word docx"));
        }
    }

    @GetMapping("/deleteAppType/{id}")
    public String delete(@CookieValue(name = "_token",defaultValue = "") String _token,@PathVariable("id") int id) throws JsonProcessingException {
        if(_token.equals("")){
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer"+ _token);
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<ResponseModel> responseFindOne = restTemplate.exchange(APPLICATION_TYPE_URL + "findOne/"+id,HttpMethod.GET,request,ResponseModel.class);
        if(responseFindOne.getStatusCode().is2xxSuccessful()){
            ResponseEntity<ResponseModel> responseDelete = restTemplate.exchange(APPLICATION_TYPE_URL + "delete/"+id,HttpMethod.DELETE,request,ResponseModel.class);
            if(responseDelete.getStatusCode().is2xxSuccessful()){
                String jsonResponse = new ObjectMapper().writeValueAsString(responseFindOne.getBody().getData());
                ApplicationType appType = new ObjectMapper().readValue(jsonResponse, new TypeReference<>() {});
                String rootPath = System.getProperty("user.dir");
                File uploadDir = new File(rootPath+ APP_TYPE_STORE_URL+appType.getName());
                if(uploadDir.exists()){
                    uploadDir.delete();
                }
                return "redirect:/dashboard/application/app_type_index";
            }else{
                return "redirect:/dashboard/application/app_type_index";
            }
        }else{
            return "redirect:/dashboard/application/app_type_index";
        }
    }

    @GetMapping("/get_one_app_type/{id}")
    @ResponseBody
    public Object getOneAppType(@CookieValue(name = "_token",defaultValue = "") String _token, @PathVariable("id") String id){
        if(_token.equals("")){
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer " + _token);
        HttpEntity request = new HttpEntity<>(headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_TYPE_URL+"findOne/"+id,HttpMethod.GET,request,ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response;
        }else{
            return response;
        }
    }

    public void uploadFile(String fileName, MultipartFile file) {
        InputStream is;
        OutputStream ot;
        String rootPath = System.getProperty("user.dir");
        File uploadDir = new File(rootPath + APP_TYPE_STORE_URL);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        try {
            ot = new FileOutputStream(uploadDir + File.separator + fileName);
            is = file.getInputStream();
            byte[] data = new byte[is.available()];
            is.read(data);
            ot.write(data);
            ot.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
