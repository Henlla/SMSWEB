package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.Devices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Devices, Integer> {
    Optional<Devices> findDevicesByAccountId(Integer accountId);
}
