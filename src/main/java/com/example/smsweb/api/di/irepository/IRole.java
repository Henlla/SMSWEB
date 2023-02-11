package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Role;

public interface IRole extends IGenericRepository<Role> {
    Role findRoleByRoleName(String roleName);
}
