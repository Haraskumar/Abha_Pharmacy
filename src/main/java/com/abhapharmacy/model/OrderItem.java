package com.abhapharmacy.model;

import java.math.BigDecimal;

public class OrderItem {
    private Integer orderId;
    private Integer itemSeqNo;
    private Integer quantity;
    private BigDecimal unitPriceAtSale;
    private Integer medicineId;
    private String batchNo;
    private String medicineName;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Integer getItemSeqNo() { return itemSeqNo; }
    public void setItemSeqNo(Integer itemSeqNo) { this.itemSeqNo = itemSeqNo; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPriceAtSale() { return unitPriceAtSale; }
    public void setUnitPriceAtSale(BigDecimal unitPriceAtSale) { this.unitPriceAtSale = unitPriceAtSale; }
    public Integer getMedicineId() { return medicineId; }
    public void setMedicineId(Integer medicineId) { this.medicineId = medicineId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
}
