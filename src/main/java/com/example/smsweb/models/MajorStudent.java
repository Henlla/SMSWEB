package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "major_student", schema = "smdb", catalog = "")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MajorStudent {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "major_id")
    private Integer majorId;
    @Basic
    @Column(name = "student_id")
    private Integer studentId;
    @ManyToOne
    @JoinColumn(name = "major_id", referencedColumnName = "id",insertable = false,updatable = false)
//    @JsonBackReference("major_major_student")
    private Major majorByMajorId;
    @ManyToOne
//    @JsonBackReference("student_major")
    @JsonIgnore
    @JoinColumn(name = "student_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Student studentByStudentId;

    public MajorStudent(Integer majorId, Integer studentId) {
        this.majorId = majorId;
        this.studentId = studentId;
    }
}
