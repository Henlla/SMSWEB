package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.DevicesService;
import com.example.smsweb.models.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device")
public class DevicesRestController {

    @Autowired
    private DevicesService service;

    @PostMapping("save")
    public ResponseEntity<?> saveDevices(@RequestBody Devices devices){
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }
}
