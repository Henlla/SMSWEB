package com.example.smsweb.api.controller;


import com.example.smsweb.api.di.irepository.IDepartment;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Department;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/department")
public class DepartmentRestController {

    @Autowired
    private IDepartment iDepartment;


    @GetMapping("/")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),iDepartment.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOneById(@PathVariable("id") Integer id){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),iDepartment.findOne(id)));
    }

    @PostMapping("/")
    public ResponseEntity<?> save(@RequestParam("departmentJSON")String departmentJson) throws JsonProcessingException {
        Department department = new ObjectMapper().readValue(departmentJson, Department.class);
        iDepartment.save(department);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),null));
    }


    @PutMapping("/")
    public ResponseEntity<?> put(@RequestParam("departmentJSON")String departmentJson) throws JsonProcessingException {
        Department department = new ObjectMapper().readValue(departmentJson, Department.class);
        iDepartment.save(department);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Integer id){
        iDepartment.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),null));
    }
}
