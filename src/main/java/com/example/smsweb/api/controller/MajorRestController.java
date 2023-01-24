package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Major;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/major")
public class MajorRestController extends GenericController<Major> {
    @Autowired
    private IMajor dao;

    @PostMapping("/save")
    public ResponseEntity<Object> save(@ModelAttribute Major major){
        try {
            dao.save(major);
           return new ResponseEntity<>("Sao lưu thành công", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Sao lưu thất bại",HttpStatus.BAD_REQUEST);
        }
    }
}
