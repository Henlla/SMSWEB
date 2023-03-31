package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "student_card")
    private String studentCard;
    @Basic
    @Column(name = "profile_id")
    private Integer profileId;

    @OneToMany(mappedBy = "studentByStudentId")
//    @JsonManagedReference("application_student")
    private List<Application> applicationsById;

    @OneToMany(mappedBy = "studentByStudentId")
//    @JsonManagedReference("student_major")
    private List<MajorStudent> majorStudentsById;

    @OneToMany(mappedBy = "studentByStudentId")
//    @JsonManagedReference("student_student_subject")

    private List<StudentSubject> studentSubjectsById;

    @OneToMany(mappedBy = "classStudentByStudent")
//    @JsonManagedReference("student_student_subject")
    @JsonIgnore
//    @JsonBackReference
    private List<StudentClass> studentClassById;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Profile studentByProfile;


    public Student(String studentCard, Integer profileId) {
        this.studentCard = studentCard;
        this.profileId = profileId;
    }
}
