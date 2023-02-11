package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RoleRepository extends GenericRepository<Role,Integer> {
    public Optional<Role> findRoleByRoleName(String roleName);
}
