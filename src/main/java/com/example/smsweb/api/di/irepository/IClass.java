package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Classses;
public interface IClass extends IGenericRepository<Classses> {
    Classses findByClassCode(String classCode);
}
