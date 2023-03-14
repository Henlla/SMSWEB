package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Application;

import java.util.List;

public interface IApplication extends IGenericRepository<Application> {
    List<Application> findApplicationByStudentId(Integer id);
}
