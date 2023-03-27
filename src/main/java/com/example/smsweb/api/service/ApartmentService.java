package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IApartment;
import com.example.smsweb.api.di.repository.ApartmentRepository;
import com.example.smsweb.models.Apartment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentService implements IApartment {

    @Autowired
    private ApartmentRepository repository;

    @Override
    public void save(Apartment apartment) {
        repository.save(apartment);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<Apartment> findAll() {
        return repository.findAll();
    }

    @Override
    public Apartment findOne(int id) {
        return repository.findById(id).orElse(null);
    }
}
