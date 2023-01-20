package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
public class Province {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Object id;
    @Basic
    @Column(name = "_name")
    private String name;
    @Basic
    @Column(name = "_code")
    private String code;
    @OneToMany(mappedBy = "districtProvince")
    @JsonManagedReference
    @JsonIgnore
    private List<District> districtsById;
    @OneToMany(mappedBy = "profileProvince")
    @JsonManagedReference
    @JsonIgnore
    private List<Profile> profilesById;
    @OneToMany(mappedBy = "wardProvince")
    @JsonManagedReference
    @JsonIgnore
    private List<Ward> wardsById;
}
