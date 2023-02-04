package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Major;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("api/major")
public class MajorRestController extends GenericController<Major> {
    @Autowired
    private IMajor service;

    @PostMapping("/save")
    public ResponseEntity<?> save(@ModelAttribute Major major) {
        try {
            service.save(major);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Success", LocalTime.now().toString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Fail", LocalTime.now().toString(), null));
        }
    }

    @PostMapping("/import-excel-data")
    public ResponseEntity<?> importExcelData(@RequestPart(required = true)MultipartFile file){
        try {
            service.importDataToDb(file);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Đỗ dữ liệu thành công",LocalTime.now().toString(),null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel("Đỗ dữ liệu thất bại",LocalTime.now().toString(),null));
        }
    }

    @GetMapping("/export-excel-data")
    public ResponseEntity<?> exportExcelData(HttpServletResponse response){
        try {
            response.setContentType("application/ms-exSheet");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=major-" + LocalTime.now().toString() + ".xlsx";
            response.setHeader(headerKey, headerValue);
            service.exportDataToExcel(response);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("Lấy dữ liệu thành công",LocalTime.now().toString(),null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseModel("Lấy dữ liệu thất bại",LocalTime.now().toString(),null));
        }
    }
}
