package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Major {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "major_code")
    private String majorCode;
    @Basic
    @Column(name = "major_name")
    private String majorName;

    @OneToMany(mappedBy = "majorByMajorId")
//    @JsonManagedReference("major_major_student")
    @JsonIgnore
    private List<MajorStudent> majorStudentsById;
    @OneToMany(mappedBy = "majorByMajorId")
//    @JsonManagedReference("subject_major")
    @JsonIgnore
    private List<Subject> subjectsById;

    @OneToMany(mappedBy = "major")
    @JsonIgnore
    private List<Classses> classesByMajorId;

}
