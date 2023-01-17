package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private Collection<Profile> profilesById;
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "_province_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Province provinceByProvinceId;
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "_district_id", referencedColumnName = "id",insertable = false,updatable = false)
    private District districtByDistrictId;
}
