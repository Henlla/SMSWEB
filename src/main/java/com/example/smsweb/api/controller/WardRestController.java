package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/wards")
public class WardRestController {
    @Autowired
    private WardService service;
    @GetMapping("/")
    public ResponseEntity<?> findAllByProvinceAndDistrict(@RequestParam("province") Integer provinceId,
                                                          @RequestParam("district") Integer districtId){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAllByProvinceAndDistrict(provinceId,districtId));
    }
}
