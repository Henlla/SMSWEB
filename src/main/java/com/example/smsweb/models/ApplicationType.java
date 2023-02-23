package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name = "application_type", schema = "smdb", catalog = "")
public class ApplicationType {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "url")
    private String url;
    @Basic
    @Column(name = "file")
    private String file;
    @OneToMany(mappedBy = "applicationTypeByApplicationTypeId")
    @JsonManagedReference("application_application_type")
    private List<Application> applicationsById;
}
