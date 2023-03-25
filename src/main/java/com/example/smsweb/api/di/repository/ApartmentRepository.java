package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Apartment;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends GenericRepository<Apartment,Integer> {
}
