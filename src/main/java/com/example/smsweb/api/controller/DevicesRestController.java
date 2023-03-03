package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.DevicesService;
import com.example.smsweb.models.Devices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/device/")
@Slf4j
public class DevicesRestController {

    @Autowired
    private DevicesService service;

    @GetMapping("getAll")
    public ResponseEntity<?> getAllDevices() throws JsonProcessingException {
        log.info("START getAllDevices " );
        log.info("FINISH getAllDevices DEVICE");
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @PostMapping("save")
    public ResponseEntity<?> saveDevices(@RequestParam("device") String devicesJson) throws JsonProcessingException {
        Devices devices = new ObjectMapper().readValue(devicesJson,Devices.class);
        log.info("START SAVE DEVICE WITH accoundId = {}",devices.getAccountId());
        service.saveDevices(devices);
        log.info("FINISH SAVE DEVICE");
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    @PostMapping("put")
    public ResponseEntity<?> putDevices(@RequestParam("device") String devicesJson) throws JsonProcessingException {
        Devices devices = new ObjectMapper().readValue(devicesJson,Devices.class);
        log.info("START PUT DEVICE WITH accoundId = {}",devices.getAccountId());
        service.saveDevices(devices);
        log.info("FINISH PUT DEVICE");
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    @GetMapping("findByAccountId/{accountId}")
    public ResponseEntity<?> findByAccountId(@PathVariable("accountId")Integer accountId){
        log.info("START find device ByAccountId WITH accoundId = {}",accountId);
       Devices devices= service.findDeviceByAccountId(accountId);
        log.info("FINISH find device ByAccountId  WITH accoundId = {}",accountId);
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }
}
