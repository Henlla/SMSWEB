package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "student_subject", schema = "smdb", catalog = "")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubject {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "subject_id")
    private Integer subjectId;
    @Basic
    @Column(name = "student_id")
    private Integer studentId;
    @Basic
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "studentSubjectByStudentSubjectId")
    @JsonBackReference(value = "attendancesById")
    private List<Attendance> attendancesById;
    @OneToMany(mappedBy = "studentSubjectByStudentSubjectId")
    @JsonBackReference(value = "marksById")
    private List<Mark> marksById;
    @ManyToOne
    @JsonManagedReference(value = "subjectBySubjectId")
    @JoinColumn(name = "subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Subject subjectBySubjectId;
    @ManyToOne
    @JsonManagedReference(value = "studentByStudentId")
    @JoinColumn(name = "student_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Student studentByStudentId;
}
