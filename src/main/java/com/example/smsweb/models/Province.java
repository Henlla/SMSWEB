package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Province {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "_name")
    private String name;
    @Basic
    @Column(name = "_code")
    private String code;
    @OneToMany(mappedBy = "districtProvince")
//    @JsonBackReference(value = "districtsById")
    @JsonIgnore
    private List<District> districtsById;
    @OneToMany(mappedBy = "profileProvince")
//    @JsonBackReference(value = "profilesById")
    @JsonIgnore
    private List<Profile> profilesById;
    @OneToMany(mappedBy = "wardProvince")
//    @JsonBackReference(value = "wardsById")
    @JsonIgnore
    private List<Ward> wardsById;
}
