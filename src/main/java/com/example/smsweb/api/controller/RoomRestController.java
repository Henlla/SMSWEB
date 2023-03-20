package com.example.smsweb.api.controller;


import com.example.smsweb.api.di.irepository.IRoom;
import com.example.smsweb.models.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/room")
public class RoomRestController {

    @Autowired
    private IRoom iRoom;

    @GetMapping("/")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(iRoom.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable("id")Integer id){
        return ResponseEntity.status(HttpStatus.OK).body(iRoom.findOne(id));
    }

    @PostMapping("/")
    public ResponseEntity<?> saveRoom(@RequestParam("room") String roomJson) throws Exception {
        Room room = new ObjectMapper().readValue(roomJson,Room.class);
        iRoom.save(room);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
