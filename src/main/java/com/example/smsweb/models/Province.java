package com.example.smsweb.models;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Province {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Object id;
    @Basic
    @Column(name = "_name")
    private String name;
    @Basic
    @Column(name = "_code")
    private String code;
    @OneToMany(mappedBy = "provinceByProvinceId")
    private Collection<District> districtsById;
    @OneToMany(mappedBy = "provinceByProvinceId")
    private Collection<Profile> profilesById;
    @OneToMany(mappedBy = "provinceByProvinceId")
    private Collection<Ward> wardsById;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Province province = (Province) o;

        if (id != null ? !id.equals(province.id) : province.id != null) return false;
        if (name != null ? !name.equals(province.name) : province.name != null) return false;
        if (code != null ? !code.equals(province.code) : province.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }

    public Collection<District> getDistrictsById() {
        return districtsById;
    }

    public void setDistrictsById(Collection<District> districtsById) {
        this.districtsById = districtsById;
    }

    public Collection<Profile> getProfilesById() {
        return profilesById;
    }

    public void setProfilesById(Collection<Profile> profilesById) {
        this.profilesById = profilesById;
    }

    public Collection<Ward> getWardsById() {
        return wardsById;
    }

    public void setWardsById(Collection<Ward> wardsById) {
        this.wardsById = wardsById;
    }
}
