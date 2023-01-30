package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import java.util.Collection;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subject {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "subject_code")
    private String subjectCode;
    @Basic
    @Column(name = "subject_name")
    private String subjectName;
    @Basic
    @Column(name = "fee")
    private Double fee;
    @Basic
    @Column(name = "slot")
    private Integer slot;
    @Basic
    @Column(name = "semester_id")
    private Integer semesterId;
    @Basic
    @Column(name = "major_id")
    private Integer majorId;
    @OneToMany(mappedBy = "subjectBySubjectId")
    @JsonManagedReference("subject_schedule_detail")
    private Collection<ScheduleDetail> scheduleDetailsById;
    @OneToMany(mappedBy = "subjectBySubjectId")
    @JsonManagedReference("subject_student_subject")
    private Collection<StudentSubject> studentSubjectsById;
    @ManyToOne
    @JoinColumn(name = "semester_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonBackReference("subject_semester")
    private Semester semesterBySemesterId;
    @ManyToOne
    @JoinColumn(name = "major_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonBackReference("subject_major")
    private Major majorByMajorId;

    public Subject(String subjectCode, String subjectName, Double fee, Integer semesterId, Integer majorId) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.fee = fee;
        this.semesterId = semesterId;
        this.majorId = majorId;
    }
}
