package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Builder(toBuilder = true)
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
    @JsonBackReference("application_student")
    @JoinColumn(name = "student_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Student studentByStudentId;
    @ManyToOne
    @JsonBackReference("application_application_type")
    @JoinColumn(name = "application_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ApplicationType applicationTypeByApplicationTypeId;
}
