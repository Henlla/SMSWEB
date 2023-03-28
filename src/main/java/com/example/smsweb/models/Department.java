package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "department_code", nullable = true, length = 45)
    private String departmentCode;
    @Basic
    @Column(name = "department_name", nullable = true, length = 100)
    private String departmentName;
    @Basic
    @Column(name = "address", nullable = true, length = 100)
    private String address;
    @Basic
    @Column(name = "phone", nullable = true, length = 45)
    private String phone;
    @OneToMany(mappedBy = "departmentByDepartmentId")
    @JsonIgnore
    private Collection<Classses> classsesById;
    @OneToMany(mappedBy = "departmentByDepartmentId")
    @JsonIgnore
    private Collection<Room> roomsById;
}
