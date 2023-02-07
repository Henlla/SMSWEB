package com.example.smsweb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "class_subject", schema = "smdb", catalog = "")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassSubject {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "subject_id")
    private Integer subjectId;
    @Basic
    @Column(name = "class_id")
    private Integer classId;
    @Basic
    @Column(name = "start_date")
    private String startDate;
    @Basic
    @Column(name = "end_date")
    private String endDate;
    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Subject subjectBySubjectId;
    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Classses classsesByClassId;
}
