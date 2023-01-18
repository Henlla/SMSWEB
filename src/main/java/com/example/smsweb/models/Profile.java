package com.example.smsweb.models;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Profile {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "first_name")
    private String firstName;
    @Basic
    @Column(name = "last_name")
    private String lastName;
    @Basic
    @Column(name = "dob")
    private String dob;
    @Basic
    @Column(name = "province_id")
    private Object provinceId;
    @Basic
    @Column(name = "district_id")
    private Object districtId;
    @Basic
    @Column(name = "ward_id")
    private Object wardId;
    @Basic
    @Column(name = "address")
    private String address;
    @Basic
    @Column(name = "phone")
    private String phone;
    @Basic
    @Column(name = "avartar_url")
    private String avartarUrl;
    @Basic
    @Column(name = "avatar_path")
    private String avatarPath;
    @Basic
    @Column(name = "identity_card")
    private String identityCard;
    @Basic
    @Column(name = "account_id")
    private Integer accountId;
    @ManyToOne
    @JoinColumn(name = "province_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Province provinceByProvinceId;
    @ManyToOne
    @JoinColumn(name = "district_id", referencedColumnName = "id",insertable = false,updatable = false)
    private District districtByDistrictId;
    @ManyToOne
    @JoinColumn(name = "ward_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Ward wardByWardId;
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Account accountByAccountId;
    @OneToMany(mappedBy = "profileByProfileId")
    private Collection<Staff> staffById;
    @OneToMany(mappedBy = "profileByProfileId")
    private Collection<Teacher> teachersById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public Object getWardId() {
        return wardId;
    }

    public void setWardId(Object wardId) {
        this.wardId = wardId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvartarUrl() {
        return avartarUrl;
    }

    public void setAvartarUrl(String avartarUrl) {
        this.avartarUrl = avartarUrl;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (id != profile.id) return false;
        if (firstName != null ? !firstName.equals(profile.firstName) : profile.firstName != null) return false;
        if (lastName != null ? !lastName.equals(profile.lastName) : profile.lastName != null) return false;
        if (dob != null ? !dob.equals(profile.dob) : profile.dob != null) return false;
        if (provinceId != null ? !provinceId.equals(profile.provinceId) : profile.provinceId != null) return false;
        if (districtId != null ? !districtId.equals(profile.districtId) : profile.districtId != null) return false;
        if (wardId != null ? !wardId.equals(profile.wardId) : profile.wardId != null) return false;
        if (address != null ? !address.equals(profile.address) : profile.address != null) return false;
        if (phone != null ? !phone.equals(profile.phone) : profile.phone != null) return false;
        if (avartarUrl != null ? !avartarUrl.equals(profile.avartarUrl) : profile.avartarUrl != null) return false;
        if (avatarPath != null ? !avatarPath.equals(profile.avatarPath) : profile.avatarPath != null) return false;
        if (identityCard != null ? !identityCard.equals(profile.identityCard) : profile.identityCard != null)
            return false;
        if (accountId != null ? !accountId.equals(profile.accountId) : profile.accountId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (dob != null ? dob.hashCode() : 0);
        result = 31 * result + (provinceId != null ? provinceId.hashCode() : 0);
        result = 31 * result + (districtId != null ? districtId.hashCode() : 0);
        result = 31 * result + (wardId != null ? wardId.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (avartarUrl != null ? avartarUrl.hashCode() : 0);
        result = 31 * result + (avatarPath != null ? avatarPath.hashCode() : 0);
        result = 31 * result + (identityCard != null ? identityCard.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        return result;
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

    public Ward getWardByWardId() {
        return wardByWardId;
    }

    public void setWardByWardId(Ward wardByWardId) {
        this.wardByWardId = wardByWardId;
    }

    public Account getAccountByAccountId() {
        return accountByAccountId;
    }

    public void setAccountByAccountId(Account accountByAccountId) {
        this.accountByAccountId = accountByAccountId;
    }

    public Collection<Staff> getStaffById() {
        return staffById;
    }

    public void setStaffById(Collection<Staff> staffById) {
        this.staffById = staffById;
    }

    public Collection<Teacher> getTeachersById() {
        return teachersById;
    }

    public void setTeachersById(Collection<Teacher> teachersById) {
        this.teachersById = teachersById;
    }
}
