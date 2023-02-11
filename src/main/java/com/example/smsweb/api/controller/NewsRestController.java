package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.INews;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.api.service.NewsService;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.News;
import com.example.smsweb.models.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("api/news")
@Slf4j
public class NewsRestController {
    @Autowired
    private NewsService service;

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestParam("news") String news, @RequestParam("file") MultipartFile file) {
        try {
            log.info("START method post news :::::::::");
            log.info("Request params news = {}",news);
            News newsConvert = new ObjectMapper().readValue(news, News.class);
            String imagePath = service.saveFile(file);
            newsConvert.setThumbnailPath(imagePath);
            News newsSave = service.save(newsConvert);
            log.info("Response news = {}",newsSave);
            log.info("FINISH method post news :::::::::");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Tạo thành công", LocalTime.now().toString(), newsSave));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Tạo thất bại", LocalTime.now().toString(), null));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> changeThumbnail(@PathVariable("id")Integer id,@RequestParam("file") MultipartFile file) throws IOException {
        News news = service.changeImageProfile(id,file);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(),news));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Lấy dữ liệu thành công", LocalTime.now().toString(), service.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Lấy dữ liệu thất bại", LocalTime.now().toString(), null));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id")Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Lấy dữ liệu thành công", LocalTime.now().toString(), service.findOne(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Lấy dữ liệu thất bại", LocalTime.now().toString(), null));
        }
    }

    @PutMapping("/put")
    public ResponseEntity<?> put(@ModelAttribute News news) {
        try {
            service.save(news);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Cập nhật thành công", LocalTime.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Cập nhật thất bại", LocalTime.now().toString(), null));
        }
    }

    @PutMapping("/put/{id}")
    public ResponseEntity<?> put(@PathVariable("id")Integer id,@RequestParam("isActive") boolean isActive) {
        try {
            News news = service.findOne(id);
            news.setIsActive(isActive);
            service.save(news);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Cập nhật thành công", LocalTime.now().toString(), news));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Cập nhật thất bại", LocalTime.now().toString(), null));
        }
    }
}
