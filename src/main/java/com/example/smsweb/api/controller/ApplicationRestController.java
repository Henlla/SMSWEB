package com.example.smsweb.api.controller;

import com.example.smsweb.api.di.irepository.IApplication;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.models.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/application")
public class ApplicationRestController extends GenericController<Application> {
    @Autowired
    public IApplication service;
}
