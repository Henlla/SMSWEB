package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name= "room_code", nullable = true, length = 50)
    private String roomCode;
    @OneToMany(mappedBy = "roomId")
    @JsonIgnore
    private List<Classses> roomClass;
    @Basic
    @Column(name = "department_id", nullable = true)
    private Integer departmentId;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id",insertable = false, updatable = false)
    private Department departmentByDepartmentId;

}
