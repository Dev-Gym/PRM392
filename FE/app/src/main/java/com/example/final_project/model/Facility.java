package com.example.final_project.model;

import com.google.gson.annotations.SerializedName;

public class Facility {
    @SerializedName("facilityId")
    private int facilityId;

    @SerializedName("facilityName")
    private String facilityName;

    @SerializedName("address")
    private String address;

    @SerializedName("service")
    private String service;

    @SerializedName("facilityType")
    private String facilityType;

    @SerializedName("verified")
    private boolean verified;

    @SerializedName("contactInfo")
    private String contactInfo;

    @SerializedName("isActive")
    private boolean isActive;

    // Constructors
    public Facility() {}

    public Facility(int facilityId, String facilityName, String address) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.address = address;
    }

    // Getters and Setters
    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper method để hiển thị trong spinner
    public String getDisplayName() {
        return facilityName + " - " + address;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}