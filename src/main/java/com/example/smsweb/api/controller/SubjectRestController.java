package com.example.smsweb.api.controller;

import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.models.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/subject")
public class SubjectRestController extends GenericController<Subject> {
}
