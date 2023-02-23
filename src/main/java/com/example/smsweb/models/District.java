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
@NoArgsConstructor
@AllArgsConstructor
public class District {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "_name")
    private String name;
    @Basic
    @Column(name = "_prefix")
    private String prefix;
    @Basic
    @Column(name = "_province_id")
    private Integer provinceId;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "_province_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Province districtProvince;
    @OneToMany(mappedBy = "districtByDistrictId")
    @JsonManagedReference("district_profile")
    @JsonIgnore
    private List<Profile> profilesById;
    @OneToMany(mappedBy = "districtByDistrictId")
    @JsonManagedReference
    @JsonIgnore
    private List<Ward> wardsById;
}
