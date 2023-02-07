package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;

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
    private Integer provinceId;
    @Basic
    @Column(name = "district_id")
    private Integer districtId;
    @Basic
    @Column(name = "ward_id")
    private Integer wardId;
    @Basic
    @Column(name = "address")
    private String address;
    @Basic
    @Column(name = "phone")
    private String phone;
    @Basic
    @Column(name = "email")
    private String email;
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
    @Column(name = "sex")
    private String sex;
    @Basic
    @Column(name = "account_id")
    private Integer accountId;
    @ManyToOne
    @JoinColumn(name = "province_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Province profileProvince;
    @ManyToOne
    @JoinColumn(name = "district_id", referencedColumnName = "id",insertable = false,updatable = false)
    private District districtByDistrictId;
    @ManyToOne
    @JoinColumn(name = "ward_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Ward wardByWardId;
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id",insertable = false,updatable = false)
//    @JsonBackReference("account_profile")
    private Account accountByAccountId;

    @OneToMany(mappedBy = "profileByProfileId",fetch=FetchType.EAGER)
    @JsonIgnore
    private List<Staff> staffById;
    @OneToMany(mappedBy = "profileByProfileId",fetch=FetchType.EAGER)
    @JsonIgnore
    private List<Teacher> teachersById;
    @OneToMany(mappedBy = "studentByProfile",fetch=FetchType.EAGER)
    @JsonIgnore
    private List<Student> studentsById;

}
