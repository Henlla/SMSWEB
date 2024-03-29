package com.example.smsweb.dto;

import com.example.smsweb.models.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RoomScheduleViewModel implements Serializable {
    private Integer id;
    private String roomCode;
    private List<Classses> roomClass;
    private Integer departmentId;
    private Department departmentByDepartmentId;

}
