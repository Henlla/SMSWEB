package com.example.smsweb.client.dashboard;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.News;
import com.example.smsweb.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("dashboard/news/")
@MultipartConfig
@Slf4j
public class NewsController {

    private final String NEWS_URL = "http://localhost:8080/api/news/";
    private final String NEW_STORE_URL = "/src/main/resources/static/application/NewsTemplate/";

    @GetMapping("create_new")
    public String create_new(@CookieValue(name = "_token", defaultValue = "") String _token) {
        try {
            JWTUtils.checkExpired(_token);
            return "dashboard/news/create_news";
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }

    }

    @PostMapping("create_news")
    @ResponseBody
    public String create_news(@CookieValue(name = "_token", defaultValue = "") String _token,
                              @RequestParam("news") String news,
                              MultipartFile file) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            News newsConvert = new ObjectMapper().readValue(news, News.class);
            newsConvert.setPost_date(LocalDate.now().toString());
            String jsonNew = new ObjectMapper().writeValueAsString(newsConvert);
            HttpHeaders header = new HttpHeaders();
            header.set("Content-Type", "multipart/form-data");
            header.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("file", file.getResource());
            params.add("news", jsonNew);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, header);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL + "post", HttpMethod.POST, request, ResponseModel.class);
            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }

    @GetMapping("index-news")
    public String index(Model model,
                        @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL + "list", ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            List<News> newsList = new ObjectMapper().readValue(json, new TypeReference<List<News>>() {
            });
            model.addAttribute("news", newsList);
            return "dashboard/news/new_index";
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @PostMapping("change_active/{id}")
    @ResponseBody
    public String change_active(@PathVariable("id") Integer id,
                                @RequestParam("isActive") String isActive,
                                @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("isActive", Boolean.parseBoolean(isActive));
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, header);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL + "put/" + id, HttpMethod.PUT, request, ResponseModel.class);
            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }

    }

    @GetMapping("new_details/{id}")
    @ResponseBody
    public Object new_details(@PathVariable("id") Integer id,
                              @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL + "get/" + id, ResponseModel.class);
            return response.getBody().getData();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }

    }

    @PostMapping("changeImg")
    @ResponseBody
    public String change_image(@CookieValue(name = "_token", defaultValue = "") String _token,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam("news_id") Integer id) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "multipart/form-data");
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("file", file.getResource());
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL + id, HttpMethod.PUT, request, ResponseModel.class);
            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }


    @PostMapping("update_new")
    @ResponseBody
    public String update_news(@CookieValue(name = "_token", defaultValue = "") String _token,
                              @RequestParam("news") String news) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            News newsConvert = new ObjectMapper().readValue(news, News.class);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("id", newsConvert.getId());
            params.add("title", newsConvert.getTitle());
            params.add("sub_title", newsConvert.getSub_title());
            params.add("post_date", newsConvert.getPost_date());
            params.add("isActive", newsConvert.getIsActive());
            params.add("content", newsConvert.getContent());
            params.add("thumbnailUrl", newsConvert.getThumbnailUrl());
            params.add("thumbnailPath", newsConvert.getThumbnailPath());
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(NEWS_URL + "put", HttpMethod.PUT, request, ResponseModel.class);
            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }

    @PostMapping("createFileTemplate")
    @ResponseBody
    public String createFileTemplate(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @RequestParam("file") MultipartFile file){
        try {
            JWTUtils.checkExpired(_token);
            String fileName = FileUtils.getFileName(file)+LocalDate.now().toString() +"."+FileNameUtils.getExtension(file.getOriginalFilename());
            FileUtils.uploadFile(fileName, NEW_STORE_URL,file);
            return "success";
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("downloadFileTemplate")
    @ResponseBody
    public Object downloadFileTemplate(@CookieValue(name = "_token", defaultValue = "") String _token){
        try {
            JWTUtils.checkExpired(_token);
            String rootPath = System.getProperty("user.dir");
           File file = FileUtils.listFilesForFolder(rootPath+NEW_STORE_URL);
            return "/application/NewsTemplate/"+file.getName();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("import_file_excel")
    @ResponseBody
    public Object import_file_excel(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @RequestParam("file") MultipartFile file){
        try {
            FileInputStream fis = new FileInputStream(file.getOriginalFilename());
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for(int i=0;i<paragraphs.size();i++){
                System.out.println(paragraphs.get(i).getParagraphText());
            }
            fis.close();
            return "success";
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
