package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IRole;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/roles")
public class RoleRestController {

    @Autowired
    private IRole iRole;

    @PostMapping("/")
    public ResponseEntity<?> saveStudent(@ModelAttribute Role role){
        iRole.save(role);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(),role));
    }


    @GetMapping("/list")
    public ResponseEntity<?> findAllStudent(){
        return ResponseEntity.status(HttpStatus.OK).body(iRole.findAll());
    }
}
