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
@Setter
@Getter
@Table(name = "student_subject", schema = "smdb", catalog = "")
@AllArgsConstructor
@NoArgsConstructor
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
    @JsonManagedReference("attendance_student_subject")
    private Collection<Attendance> attendancesById;
    @OneToMany(mappedBy = "studentSubjectByStudentSubjectId")
    @JsonManagedReference
    private Collection<Mark> marksById;
    @ManyToOne
    @JsonBackReference("subject_student_subject")
    @JoinColumn(name = "subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Subject subjectBySubjectId;
    @ManyToOne
    @JsonBackReference("student_student_subject")
    @JoinColumn(name = "student_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Student studentByStudentId;

    public StudentSubject(Integer subjectId, Integer studentId, String status) {
        this.subjectId = subjectId;
        this.studentId = studentId;
        this.status = status;
    }
}
