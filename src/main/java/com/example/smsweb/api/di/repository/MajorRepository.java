package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Major;

public interface MajorRepository extends GenericRepository<Major, Integer> {
    Major findMajorByMajorCode(String major_code);

    Major findMajorByMajorName(String major_name);
}
