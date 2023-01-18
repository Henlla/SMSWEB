package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "application_type", schema = "smdb", catalog = "")
public class ApplicationType {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "applicationTypeByApplicationTypeId")
    @JsonBackReference(value = "applicationsById")
    private List<Application> applicationsById;
}
