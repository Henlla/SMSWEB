package com.example.smsweb.models;

import jakarta.persistence.*;

import java.util.Collection;

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
    private Collection<Application> applicationsById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationType that = (ApplicationType) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public Collection<Application> getApplicationsById() {
        return applicationsById;
    }

    public void setApplicationsById(Collection<Application> applicationsById) {
        this.applicationsById = applicationsById;
    }
}
