package com.abhapharmacy.model;

import java.util.ArrayList;
import java.util.List;

public class AiPrescriptionAnalysis {
    private String doctorName;
    private String doctorRegNo;
    private String patientName;
    private String diagnosis;
    private String confidenceScore;
    private String clinicalGuidance;
    private List<MatchedItem> matchedItems = new ArrayList<>();

    public static class MatchedItem {
        private Integer medicineId;
        private String medicineName;
        private String genericName;
        private Integer suggestedQuantity;
        private String dosageInstructions;
        private boolean inStock;

        public MatchedItem() {}

        public MatchedItem(Integer medicineId, String medicineName, String genericName, Integer suggestedQuantity, String dosageInstructions, boolean inStock) {
            this.medicineId = medicineId;
            this.medicineName = medicineName;
            this.genericName = genericName;
            this.suggestedQuantity = suggestedQuantity;
            this.dosageInstructions = dosageInstructions;
            this.inStock = inStock;
        }

        public Integer getMedicineId() { return medicineId; }
        public void setMedicineId(Integer medicineId) { this.medicineId = medicineId; }
        public String getMedicineName() { return medicineName; }
        public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
        public String getGenericName() { return genericName; }
        public void setGenericName(String genericName) { this.genericName = genericName; }
        public Integer getSuggestedQuantity() { return suggestedQuantity; }
        public void setSuggestedQuantity(Integer suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }
        public String getDosageInstructions() { return dosageInstructions; }
        public void setDosageInstructions(String dosageInstructions) { this.dosageInstructions = dosageInstructions; }
        public boolean isInStock() { return inStock; }
        public void setInStock(boolean inStock) { this.inStock = inStock; }
    }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorRegNo() { return doctorRegNo; }
    public void setDoctorRegNo(String doctorRegNo) { this.doctorRegNo = doctorRegNo; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(String confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getClinicalGuidance() { return clinicalGuidance; }
    public void setClinicalGuidance(String clinicalGuidance) { this.clinicalGuidance = clinicalGuidance; }
    public List<MatchedItem> getMatchedItems() { return matchedItems; }
    public void setMatchedItems(List<MatchedItem> matchedItems) { this.matchedItems = matchedItems; }
}
