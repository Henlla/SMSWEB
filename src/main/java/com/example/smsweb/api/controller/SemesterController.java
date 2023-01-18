package com.example.smsweb.api.controller;

import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.models.Semester;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/semester")
public class SemesterController extends GenericController<Semester> {
}
