package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Student {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "student_card")
    private String studentCard;
    @Basic
    @Column(name = "profile_id")
    private Integer profileId;

    @OneToMany(mappedBy = "studentByStudentId")
    @JsonManagedReference("application_student")
    private Collection<Application> applicationsById;

    @OneToMany(mappedBy = "studentByStudentId")
    @JsonManagedReference("student_major")
    private Collection<MajorStudent> majorStudentsById;

    @OneToMany(mappedBy = "studentByStudentId")
    @JsonManagedReference("student_student_subject")
    private Collection<StudentSubject> studentSubjectsById;

    @OneToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Profile studentByProfile;


    public Student(String studentCard, Integer profileId) {
        this.studentCard = studentCard;
        this.profileId = profileId;
    }
}
