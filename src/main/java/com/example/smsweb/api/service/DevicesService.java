package com.example.smsweb.api.service;

import com.example.smsweb.api.di.repository.DeviceRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevicesService {
    @Autowired
    private DeviceRepository repository;

    public void saveDevices(Devices devices){
        repository.save(devices);
    }
    public Devices findDeviceByAccountId(Integer accountId){
       return repository.findDevicesByAccountId(accountId).orElseThrow(()->new ErrorHandler("Cannot find devices with accountId = "+accountId));
    }

    public List<Devices> findAll(){
        return repository.findAll();
    }
}
