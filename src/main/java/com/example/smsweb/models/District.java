package com.example.smsweb.models;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class District {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Object id;
    @Basic
    @Column(name = "_name")
    private String name;
    @Basic
    @Column(name = "_prefix")
    private String prefix;
    @Basic
    @Column(name = "_province_id")
    private Object provinceId;
    @ManyToOne
    @JoinColumn(name = "_province_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Province provinceByProvinceId;
    @OneToMany(mappedBy = "districtByDistrictId")
    private Collection<Profile> profilesById;
    @OneToMany(mappedBy = "districtByDistrictId")
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Object getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Object provinceId) {
        this.provinceId = provinceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        District district = (District) o;

        if (id != null ? !id.equals(district.id) : district.id != null) return false;
        if (name != null ? !name.equals(district.name) : district.name != null) return false;
        if (prefix != null ? !prefix.equals(district.prefix) : district.prefix != null) return false;
        if (provinceId != null ? !provinceId.equals(district.provinceId) : district.provinceId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (provinceId != null ? provinceId.hashCode() : 0);
        return result;
    }

    public Province getProvinceByProvinceId() {
        return provinceByProvinceId;
    }

    public void setProvinceByProvinceId(Province provinceByProvinceId) {
        this.provinceByProvinceId = provinceByProvinceId;
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
