package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.INews;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("dashboard/api/news")
public class NewsRestController extends GenericController<News> {
    @Autowired
    private INews service;

    @PostMapping("/post")
    public ResponseEntity<?> post(@ModelAttribute News news) {
        try {
            service.save(news);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Tạo thành công", LocalTime.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Tạo thất bại", LocalTime.now().toString(), null));
        }
    }
}
