package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.DeviceRepository;
import com.example.smsweb.models.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevicesService {
    @Autowired
    private DeviceRepository repository;

    public void saveDevices(Devices devices){
        repository.save(devices);
    }
}
