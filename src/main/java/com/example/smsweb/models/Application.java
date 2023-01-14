package com.example.smsweb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "send_date")
    private String sendDate;
    @Basic
    @Column(name = "status")
    private String status;
    @Basic
    @Column(name = "file")
    private String file;
    @Basic
    @Column(name = "student_id")
    private Integer studentId;
    @Basic
    @Column(name = "application_type_id")
    private Integer applicationTypeId;
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Student studentByStudentId;
    @ManyToOne
    @JoinColumn(name = "application_type_id", referencedColumnName = "id",insertable = false,updatable = false)
    private ApplicationType applicationTypeByApplicationTypeId;

}
