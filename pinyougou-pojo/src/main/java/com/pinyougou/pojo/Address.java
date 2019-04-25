package com.pinyougou.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="tb_address")
public class Address implements Serializable{

	private static final long serialVersionUID = -4689694958239207095L;
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="user_id")
    private String userId;
	@Column(name="province_id")
    private String provinceId;//省份表
	@Column(name="city_id")
    private String cityId;//城市表
	@Column(name="town_id")
    private String townId;//区域表
	@Column(name="mobile")
    private String mobile;
	@Column(name="address")
    private String address;
	@Column(name="contact")
    private String contact;
	@Column(name="is_default")
    private String isDefault;
	@Column(name="notes")
    private String notes;
	@Column(name="create_date")
    private Date createDate;
	@Column(name="alias")
    private String alias;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId == null ? null : provinceId.trim();
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId == null ? null : cityId.trim();
    }

    public String getTownId() {
        return townId;
    }

    public void setTownId(String townId) {
        this.townId = townId == null ? null : townId.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact == null ? null : contact.trim();
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault == null ? null : isDefault.trim();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias == null ? null : alias.trim();
    }
}