package com.abhapharmacy.model;

import java.time.LocalDate;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;

public class Batch {
    private Integer medicineId;
    private String batchNo;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mfgDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    
    private Integer quantityInStock;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private String rackLocation;
    private Integer supplierId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate supplyDate;
    private BigDecimal purchaseAmount;

    public Integer getMedicineId() { return medicineId; }
    public void setMedicineId(Integer medicineId) { this.medicineId = medicineId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public LocalDate getMfgDate() { return mfgDate; }
    public void setMfgDate(LocalDate mfgDate) { this.mfgDate = mfgDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Integer getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(Integer quantityInStock) { this.quantityInStock = quantityInStock; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public String getRackLocation() { return rackLocation; }
    public void setRackLocation(String rackLocation) { this.rackLocation = rackLocation; }
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public LocalDate getSupplyDate() { return supplyDate; }
    public void setSupplyDate(LocalDate supplyDate) { this.supplyDate = supplyDate; }
    public BigDecimal getPurchaseAmount() { return purchaseAmount; }
    public void setPurchaseAmount(BigDecimal purchaseAmount) { this.purchaseAmount = purchaseAmount; }
}
