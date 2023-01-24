package com.example.smsweb.api.controller;

import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.models.Major;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/major")
public class MajorRestController extends GenericController<Major> {
}
