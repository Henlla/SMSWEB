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
public class Profile {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "first_name")
    private String firstName;
    @Basic
    @Column(name = "last_name")
    private String lastName;
    @Basic
    @Column(name = "dob")
    private String dob;
    @Basic
    @Column(name = "province_id")
    private Object provinceId;
    @Basic
    @Column(name = "district_id")
    private Object districtId;
    @Basic
    @Column(name = "ward_id")
    private Object wardId;
    @Basic
    @Column(name = "address")
    private String address;
    @Basic
    @Column(name = "phone")
    private String phone;
    @Basic
    @Column(name = "avartar_url")
    private String avartarUrl;
    @Basic
    @Column(name = "avatar_path")
    private String avatarPath;
    @Basic
    @Column(name = "identity_card")
    private String identityCard;
    @Basic
    @Column(name = "account_id")
    private Integer accountId;
    @ManyToOne
    @JoinColumn(name = "province_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonManagedReference
    private Province provinceByProvinceId;
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "district_id", referencedColumnName = "id",insertable = false,updatable = false)
    private District districtByDistrictId;
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "ward_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Ward wardByWardId;
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "account_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Account accountByAccountId;
    @OneToMany(mappedBy = "profileByProfileId")
    @JsonBackReference
    private Collection<Staff> staffById;
    @OneToMany(mappedBy = "profileByProfileId")
    @JsonBackReference
    private Collection<Teacher> teachersById;
}
