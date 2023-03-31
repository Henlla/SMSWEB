package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IDepartment;
import com.example.smsweb.api.di.repository.DepartmentRepository;
import com.example.smsweb.models.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DepartmentService implements IDepartment {

    @Autowired
    private DepartmentRepository repository;

    @Override
    public void save(Department department) {
        repository.save(department);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<Department> findAll() {
        return repository.findAll();
    }

    @Override
    public Department findOne(int id) {
        return repository.findById(id).orElse(null);
    }
}
