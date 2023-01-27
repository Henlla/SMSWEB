package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.*;
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
@AllArgsConstructor
@NoArgsConstructor
public class Province {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
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
