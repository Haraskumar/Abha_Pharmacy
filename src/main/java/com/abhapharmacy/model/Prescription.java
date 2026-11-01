package com.abhapharmacy.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Prescription {
    private Integer prescriptionId;
    private String prescriptionImageUrl;
    private LocalDate uploadDate;
    private LocalTime uploadTime;
    private String doctorName;
    private String patientName;
    private String verificationStatus;
    private String pharmacistRemarks;
    private String instructions;
    private Integer customerId;
    private Integer staffId;
    private LocalDate verificationDate;
    private LocalTime verificationTime;
    private Customer customer;

    public Integer getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Integer prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getPrescriptionImageUrl() { return prescriptionImageUrl; }
    public void setPrescriptionImageUrl(String prescriptionImageUrl) { this.prescriptionImageUrl = prescriptionImageUrl; }
    public String getPrescriptionImageURL() { return prescriptionImageUrl; }
    public void setPrescriptionImageURL(String prescriptionImageURL) { this.prescriptionImageUrl = prescriptionImageURL; }
    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }
    public LocalTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalTime uploadTime) { this.uploadTime = uploadTime; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public String getPharmacistRemarks() { return pharmacistRemarks; }
    public void setPharmacistRemarks(String pharmacistRemarks) { this.pharmacistRemarks = pharmacistRemarks; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public LocalDate getVerificationDate() { return verificationDate; }
    public void setVerificationDate(LocalDate verificationDate) { this.verificationDate = verificationDate; }
    public LocalTime getVerificationTime() { return verificationTime; }
    public void setVerificationTime(LocalTime verificationTime) { this.verificationTime = verificationTime; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
