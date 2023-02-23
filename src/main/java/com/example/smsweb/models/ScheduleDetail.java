package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "schedule_detail", schema = "smdb", catalog = "")
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

    @Basic
    @Column(name = "day_of_week")
    private String dayOfWeek;

    @ManyToOne
    @JsonBackReference("subject_schedule_detail")
    @JoinColumn(name = "subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Subject subjectBySubjectId;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "schedule_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Schedule scheduleByScheduleId;
}
