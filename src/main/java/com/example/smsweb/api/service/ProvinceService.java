package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IProvince;
import com.example.smsweb.api.di.repository.ProvinceRepository;
import com.example.smsweb.models.Province;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProvinceService implements IProvince {
    @Autowired
    private ProvinceRepository repository;
    @Override
    public void save(Province province) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Province> findAll() {
        return repository.findAll();
    }

    @Override
    public Province findOne(int id) {
        return null;
    }
}
