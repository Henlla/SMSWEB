package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IRole;
import com.example.smsweb.api.di.repository.RoleRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleService implements IRole {

    @Autowired
    private RoleRepository repository;

    @Override
    public void save(Role role) {
        try {
            repository.save(role);
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            repository.deleteById(id);
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public List<Role> findAll() {
        try {
            return repository.findAll();
        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }

    }

    @Override
    public Role findOne(int id) {
        return repository.findById(id).orElseThrow(()->new ErrorHandler("Cannot find role with id = "+id));
    }
}
