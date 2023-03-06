package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Teacher {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "profile_id")
    private Integer profileId;
    @Basic
    @Column(name = "teacher_card")
    private String teacherCard;
    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Profile profileByProfileId;

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<Classses> teacherClass;

}
