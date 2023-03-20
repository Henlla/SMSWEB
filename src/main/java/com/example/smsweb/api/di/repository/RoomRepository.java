package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Account;
import com.example.smsweb.models.Room;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends GenericRepository<Room,Integer> {

}
