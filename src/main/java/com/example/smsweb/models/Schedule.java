package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
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
    @JsonBackReference("class_schedule")
    @JoinColumn(name = "class_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Classses classsesByClassId;
    @OneToMany(mappedBy = "scheduleByScheduleId")
    @JsonManagedReference
    private Collection<ScheduleDetail> scheduleDetailsById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        if (id != schedule.id) return false;
        if (startDate != null ? !startDate.equals(schedule.startDate) : schedule.startDate != null) return false;
        if (endDate != null ? !endDate.equals(schedule.endDate) : schedule.endDate != null) return false;
        if (classId != null ? !classId.equals(schedule.classId) : schedule.classId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (classId != null ? classId.hashCode() : 0);
        return result;
    }

    public Classses getClasssesByClassId() {
        return classsesByClassId;
    }

    public void setClasssesByClassId(Classses classsesByClassId) {
        this.classsesByClassId = classsesByClassId;
    }

    public Collection<ScheduleDetail> getScheduleDetailsById() {
        return scheduleDetailsById;
    }

    public void setScheduleDetailsById(Collection<ScheduleDetail> scheduleDetailsById) {
        this.scheduleDetailsById = scheduleDetailsById;
    }
}
