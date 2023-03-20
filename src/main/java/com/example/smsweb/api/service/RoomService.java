package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IRoom;
import com.example.smsweb.api.di.repository.RoomRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService implements IRoom {

    @Autowired
    private RoomRepository roomRepository;


    @Override
    public void save(Room room) {
        roomRepository.save(room);
    }

    @Override
    public void delete(int id){
      roomRepository.deleteById(id);
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findOne(int id) {
        return roomRepository.findById(id).orElseThrow(()->new ErrorHandler("Cannot find room with id = "+id));
    }


}
