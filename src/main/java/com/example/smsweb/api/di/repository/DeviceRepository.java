package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.Devices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Devices, Integer> {
}
