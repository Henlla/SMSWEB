package com.example.smsweb.client.dashboard;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.News;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("dashboard/news/")
@MultipartConfig
public class NewsController {

    private final String NEWS_URL = "http://localhost:8080/api/news/";

    @GetMapping("create_new")
    public String create_new(){
        return "dashboard/news/create_news";
    }

    @PostMapping("create_news")
    @ResponseBody
    public String create_news(@CookieValue(name = "_token", defaultValue = "") String _token,
                              @RequestParam("news")String news ,
                              MultipartFile file) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        News newsConvert = new ObjectMapper().readValue(news,News.class);
        newsConvert.setPost_date(LocalDate.now().toString());
        String jsonNew = new ObjectMapper().writeValueAsString(newsConvert);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "multipart/form-data");
        header.set("Authorization","Bearer "+_token);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", file.getResource());
        params.add("news", jsonNew);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, header);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL+"post", HttpMethod.POST,request,ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return "failed";
        }
    }

    @GetMapping("index-news")
    public String index(Model model) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL+"list",ResponseModel.class);
        String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
        List<News> newsList = new ObjectMapper().readValue(json, new TypeReference<List<News>>() {
        });
        model.addAttribute("news",newsList);
        return "dashboard/news/new_index";
    }

    @PostMapping("change_active/{id}")
    @ResponseBody
    public String change_active(@PathVariable("id")Integer id,
                                @RequestParam("isActive")String isActive,
                                @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header =new HttpHeaders();
        header.set("Authorization","Bearer "+_token);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("isActive", Boolean.parseBoolean(isActive));
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, header);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL+"put/"+id,HttpMethod.PUT,request,ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return "false";
        }

    }

    @GetMapping("new_details/{id}")
    @ResponseBody
    public Object new_details(@PathVariable("id")Integer id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL+"get/"+id,ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody().getData();
        }else{
            return "false";
        }

    }

    @PostMapping("changeImg")
    @ResponseBody
    public String change_image(@CookieValue(name = "_token", defaultValue = "") String _token,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam("news_id") Integer id){
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
        ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL+id, HttpMethod.PUT, request, ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return "failed";
        }
    }


    @PostMapping("update_new")
    @ResponseBody
    public String update_news(@CookieValue(name = "_token", defaultValue = "") String _token,
                              @RequestParam("news")String news) throws JsonProcessingException {
        JWTUtils.checkExpired(_token);
        RestTemplate restTemplate = new RestTemplate();
        News newsConvert = new ObjectMapper().readValue(news,News.class);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("id",newsConvert.getId());
        params.add("title",newsConvert.getTitle());
        params.add("sub_title",newsConvert.getSub_title());
        params.add("post_date",newsConvert.getPost_date());
        params.add("isActive",newsConvert.getIsActive());
        params.add("content",newsConvert.getContent());
        params.add("thumbnailUrl",newsConvert.getThumbnailUrl());
        params.add("thumbnailPath",newsConvert.getThumbnailPath());
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL+"put", HttpMethod.PUT, request, ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return "failed";
        }
    }
}
