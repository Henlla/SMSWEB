package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schedule_detail", schema = "smdb", catalog = "")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetail {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "date")
    private String date;
    @Basic
    @Column(name = "subject_id")
    private Integer subjectId;
    @Basic
    @Column(name = "schedule_id")
    private Integer scheduleId;
    @ManyToOne
    @JsonManagedReference(value = "subjectBySubjectId")
    @JoinColumn(name = "subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Subject subjectBySubjectId;
    @ManyToOne
    @JsonManagedReference(value = "scheduleByScheduleId")
    @JoinColumn(name = "schedule_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Schedule scheduleByScheduleId;

}
