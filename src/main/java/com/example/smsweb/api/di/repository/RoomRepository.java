package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Room;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends GenericRepository<Room,Integer> {
    Optional<List<Room>> findRoomsByDepartmentId(Integer departmentId);
}
