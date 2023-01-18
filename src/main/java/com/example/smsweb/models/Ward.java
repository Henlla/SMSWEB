package com.example.smsweb.models;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Ward {
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
    @Basic
    @Column(name = "_district_id")
    private Object districtId;
    @OneToMany(mappedBy = "wardByWardId")
    private Collection<Profile> profilesById;
    @ManyToOne
    @JoinColumn(name = "_province_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Province provinceByProvinceId;
    @ManyToOne
    @JoinColumn(name = "_district_id", referencedColumnName = "id",insertable = false,updatable = false)
    private District districtByDistrictId;

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

    public Object getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Object districtId) {
        this.districtId = districtId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ward ward = (Ward) o;

        if (id != null ? !id.equals(ward.id) : ward.id != null) return false;
        if (name != null ? !name.equals(ward.name) : ward.name != null) return false;
        if (prefix != null ? !prefix.equals(ward.prefix) : ward.prefix != null) return false;
        if (provinceId != null ? !provinceId.equals(ward.provinceId) : ward.provinceId != null) return false;
        if (districtId != null ? !districtId.equals(ward.districtId) : ward.districtId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (provinceId != null ? provinceId.hashCode() : 0);
        result = 31 * result + (districtId != null ? districtId.hashCode() : 0);
        return result;
    }

    public Collection<Profile> getProfilesById() {
        return profilesById;
    }

    public void setProfilesById(Collection<Profile> profilesById) {
        this.profilesById = profilesById;
    }

    public Province getProvinceByProvinceId() {
        return provinceByProvinceId;
    }

    public void setProvinceByProvinceId(Province provinceByProvinceId) {
        this.provinceByProvinceId = provinceByProvinceId;
    }

    public District getDistrictByDistrictId() {
        return districtByDistrictId;
    }

    public void setDistrictByDistrictId(District districtByDistrictId) {
        this.districtByDistrictId = districtByDistrictId;
    }
}
