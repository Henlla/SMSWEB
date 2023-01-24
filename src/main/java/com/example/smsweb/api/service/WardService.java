package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.WardRepository;
import com.example.smsweb.models.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WardService {
    @Autowired
    private WardRepository repository;

    public List<Ward>findAllByProvinceAndDistrict(Integer provinceId,Integer districtId){
        return  repository.findAllByProvinceIdAndDistrictId(provinceId,districtId);
    }
}
