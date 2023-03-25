package com.example.smsweb.api.controller;


import com.example.smsweb.api.di.irepository.IApartment;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Apartment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/apartment")
public class ApartmentRestController {
    @Autowired
    private IApartment iApartment;


    @PostMapping("/")
    public ResponseEntity<?> saveApartment(@RequestParam("apartment")String apartmentJSON) throws JsonProcessingException {
        Apartment apartment = new ObjectMapper().readValue(apartmentJSON,Apartment.class);
        iApartment.save(apartment);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),apartment));
    }

    @GetMapping("/")
    public ResponseEntity<?> findAll() throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),iApartment.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Integer id) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),iApartment.findOne(id)));
    }

    @PutMapping("/")
    public ResponseEntity<?> putApartment(@RequestParam("apartment")String apartmentJSON) throws JsonProcessingException {
        Apartment apartment = new ObjectMapper().readValue(apartmentJSON,Apartment.class);
        iApartment.save(apartment);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalDate.now().toString(),apartment));
    }

}
