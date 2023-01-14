package com.example.smsweb.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "start_date")
    private String startDate;
    @Basic
    @Column(name = "end_date")
    private String endDate;
    @Basic
    @Column(name = "class_id")
    private Integer classId;
    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Classses classsesByClassId;
    @OneToMany(mappedBy = "scheduleByScheduleId")
    private Collection<ScheduleDetail> scheduleDetailsById;
}
