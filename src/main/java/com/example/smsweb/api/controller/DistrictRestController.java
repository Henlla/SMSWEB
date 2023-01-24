package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.DistrictService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/districts")
public class DistrictRestController {
    @Autowired
    private DistrictService service;

    @GetMapping("/")
    public ResponseEntity<?> findAll(@RequestParam("province") Integer id){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAllByProvince(id));
    }
}
