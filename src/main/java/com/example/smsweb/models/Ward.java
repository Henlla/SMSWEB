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
public class Ward {
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
    private int provinceId;
    @Basic
    @Column(name = "_district_id")
    private int districtId;
    @OneToMany(mappedBy = "wardByWardId")
    @JsonIgnore
    private List<Profile> profilesById;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "_province_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Province wardProvince;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "_district_id", referencedColumnName = "id",insertable = false,updatable = false)
    private District districtByDistrictId;
}
