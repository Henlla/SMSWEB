package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Room;

import java.util.List;

public interface IRoom extends IGenericRepository<Room> {
    List<Room> findRoomsByDepartmentId(Integer departmentId);
}
