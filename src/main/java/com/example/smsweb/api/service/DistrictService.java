package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.DistrictRepository;
import com.example.smsweb.models.District;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DistrictService {

    @Autowired
    private DistrictRepository repository;

    public List<District> findAllByProvince(Integer provinceId){
        return repository.findAll().stream().filter(district -> district.getProvinceId() == provinceId).toList();
    }
}
