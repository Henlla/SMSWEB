package com.example.smsweb.models;

import jakarta.persistence.*;

@Entity
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
    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Subject subjectBySubjectId;
    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Schedule scheduleByScheduleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleDetail that = (ScheduleDetail) o;

        if (id != that.id) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (subjectId != null ? !subjectId.equals(that.subjectId) : that.subjectId != null) return false;
        if (scheduleId != null ? !scheduleId.equals(that.scheduleId) : that.scheduleId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (subjectId != null ? subjectId.hashCode() : 0);
        result = 31 * result + (scheduleId != null ? scheduleId.hashCode() : 0);
        return result;
    }

    public Subject getSubjectBySubjectId() {
        return subjectBySubjectId;
    }

    public void setSubjectBySubjectId(Subject subjectBySubjectId) {
        this.subjectBySubjectId = subjectBySubjectId;
    }

    public Schedule getScheduleByScheduleId() {
        return scheduleByScheduleId;
    }

    public void setScheduleByScheduleId(Schedule scheduleByScheduleId) {
        this.scheduleByScheduleId = scheduleByScheduleId;
    }
}
