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
    private Integer id;
    @Basic
    @Column(name = "_name")
    private String name;
    @Basic
    @Column(name = "_prefix")
    private String prefix;
    @Basic
    @Column(name = "_province_id")
    private Integer provinceId;
    @Basic
    @Column(name = "_district_id")
    private Integer districtId;
    @OneToMany(mappedBy = "wardByWardId")
    @JsonManagedReference
    @JsonIgnore
    private List<Profile> profilesById;
    @ManyToOne
    @JsonBackReference
    @JsonIgnore
    @JoinColumn(name = "_province_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Province wardProvince;
    @ManyToOne
    @JsonBackReference
    @JsonIgnore
    @JoinColumn(name = "_district_id", referencedColumnName = "id",insertable = false,updatable = false)
    private District districtByDistrictId;

}
