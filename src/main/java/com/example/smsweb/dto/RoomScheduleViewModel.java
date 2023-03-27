package com.example.smsweb.dto;

import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Room;
import com.example.smsweb.models.Schedule;
import com.example.smsweb.models.ScheduleDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RoomScheduleViewModel implements Serializable {
    private int roomId;

    private String roomName;

    private int classId;
    private String classCode;
    private ScheduleDetail scheduleDetail;

    public RoomScheduleViewModel(Room room, Classses clazz, Schedule schedule, ScheduleDetail scheduleDetail) {
        this.classId = clazz.getId();
        this.classCode = clazz.getClassCode();
        this.roomId = room.getId();
        this.roomName = room.getRoomCode();
        this.scheduleDetail = scheduleDetail;
    }
    public RoomScheduleViewModel(Room room) {
        this.roomId = room.getId();
        this.roomName = room.getRoomCode();
    }
    public RoomScheduleViewModel(Room room,Classses clazz) {
        this.roomId = room.getId();
        this.roomName = room.getRoomCode();
        this.classId = clazz.getId();
        this.classCode = clazz.getClassCode();
    }
}
