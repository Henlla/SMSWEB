package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward,Integer> {
    public List<Ward> findAllByProvinceIdAndDistrictId(Integer provinceId,Integer districtId);
}
