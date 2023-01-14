package com.example.smsweb.Models;

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
    @Basic
    @Column(name = "subject_id")
    private Integer subjectId;
    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Subject subjectBySubjectId;
    @OneToMany(mappedBy = "majorByMajorId")
    private Collection<MajorStudent> majorStudentsById;
    @OneToMany(mappedBy = "majorByMajorId")
    private Collection<Semester> semestersById;

}
