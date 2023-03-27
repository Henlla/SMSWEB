package com.example.smsweb.api.controller;

import com.example.smsweb.api.service.ProfileService;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Profile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/profiles")
@Slf4j
public class ProfileRestController {
    @Autowired
    private ProfileService service;

    @PostMapping("/")
    public ResponseEntity<?> saveProfile(@RequestParam("profile") String profile,
                                         @RequestParam("file") MultipartFile file) {
        try {
            log.info("START method saveProfile :::::::::");
            log.info("Request params profile = {}",profile);
            Profile profileConvert = new ObjectMapper().readValue(profile, Profile.class);
            String imagePath = service.saveFile(file);
            profileConvert.setAvatarPath(imagePath);
            Profile saveProfile = service.save(profileConvert);
            log.info("Response account = {}",saveProfile);
            log.info("FINISH method saveProfile :::::::::");
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseModel("success", LocalDateTime.now().toString(),saveProfile));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel(e.getMessage(), LocalDateTime.now().toString(),null));
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> putProfile(@RequestParam("profile") String profile) throws JsonProcessingException {
        log.info("START method putProfile :::::::::");
        Profile profileConvert = new ObjectMapper().readValue(profile, Profile.class);
            service.update(profileConvert);
        log.info("FINISH method putProfile :::::::::");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(),profile));

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changeAvatarProfile(@PathVariable("id")Integer id,@RequestParam("file") MultipartFile file) throws IOException {
        Profile profileChange = service.changeImageProfile(id,file);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(),profileChange));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable("id")Integer id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(),null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id")Integer id) {
        log.info("START method getProfile id = {} :::::::::",id);
       Profile profile= service.findOne(id);
        log.info("FINISH method getProfile :::::::::");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDateTime.now().toString(),profile));
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getProfileByAccountId(@PathVariable("id")Integer id) {
        log.info("START method getProfileByAccountId id = {} :::::::::",id);
        Profile profile= service.findProfileByAccountId(id);
        log.info("FINISH method getProfileByAccountId :::::::::");
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }
}
