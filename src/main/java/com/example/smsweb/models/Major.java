package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Major {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "major_code", nullable = true, length = 45)
    private String majorCode;
    @Basic
    @Column(name = "major_name", nullable = true, length = 45)
    private String majorName;
    @OneToMany(mappedBy = "majorByMajorId")
//    @JsonManagedReference("major_major_student")
    @JsonIgnore
    private List<MajorStudent> majorStudentsById;
    @OneToMany(mappedBy = "majorByMajorId")
//    @JsonManagedReference("subject_major")
//    @JsonIgnore
    private List<Subject> subjectsById;
    @OneToMany(mappedBy = "major")
    @JsonIgnore
    private List<Classses> classesByMajorId;
    @Basic
    @Column(name = "apartment_id", nullable = true)
    private Integer apartmentId;
    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Apartment apartmentByApartmentId;
}
