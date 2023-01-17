package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

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
    @OneToMany(mappedBy = "provinceByProvinceId")
    @JsonBackReference
    private Collection<District> districtsById;
    @OneToMany(mappedBy = "provinceByProvinceId")
    @JsonBackReference
    private Collection<Profile> profilesById;
    @OneToMany(mappedBy = "provinceByProvinceId")
    @JsonBackReference
    private Collection<Ward> wardsById;
}
