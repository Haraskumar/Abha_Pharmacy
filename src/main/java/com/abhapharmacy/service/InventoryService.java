package com.abhapharmacy.service;

import com.abhapharmacy.model.Alert;
import com.abhapharmacy.model.Batch;
import com.abhapharmacy.model.Medicine;
import com.abhapharmacy.repository.AlertRepository;
import com.abhapharmacy.repository.BatchRepository;
import com.abhapharmacy.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {

    private final MedicineRepository medicineRepository;
    private final BatchRepository batchRepository;
    private final AlertRepository alertRepository;
    private final com.abhapharmacy.repository.SupplierRepository supplierRepository;

    public InventoryService(MedicineRepository medicineRepository, BatchRepository batchRepository, AlertRepository alertRepository, com.abhapharmacy.repository.SupplierRepository supplierRepository) {
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.alertRepository = alertRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<Medicine> searchMedicines(String keyword) {
        return medicineRepository.searchByName(keyword);
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine getMedicineById(Integer id) {
        return medicineRepository.findById(id);
    }

    public List<Medicine> searchOTCMedicines(String keyword) {
        return medicineRepository.searchOTCByName(keyword);
    }

    public List<Medicine> getAllOTCMedicines() {
        return medicineRepository.findAllOTC();
    }

    public List<Batch> getBatchesForMedicine(Integer medicineId) {
        return batchRepository.findByMedicineId(medicineId);
    }

    public void saveMedicine(Medicine m) {
        if (m.getReorderThreshold() != null && m.getReorderThreshold() < 0) {
            throw new IllegalArgumentException("Reorder threshold cannot be negative.");
        }
        medicineRepository.save(m);
    }

    @Transactional
    public void saveBatch(Batch b) {
        if (b.getQuantityInStock() < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
        if (b.getCostPrice().compareTo(java.math.BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Cost price cannot be negative.");
        if (b.getSellingPrice().compareTo(java.math.BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Selling price cannot be negative.");
        if (b.getMfgDate() != null && b.getExpiryDate() != null && b.getMfgDate().isAfter(b.getExpiryDate())) {
            throw new IllegalArgumentException("Manufacturing date cannot be after expiry date.");
        }
        if (b.getExpiryDate() != null && b.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot add a batch that is already expired.");
        }

        b.setPurchaseAmount(b.getCostPrice().multiply(java.math.BigDecimal.valueOf(b.getQuantityInStock())));
        batchRepository.upsertBatch(b);
    }

    @Transactional
    public void checkAndCreateAlerts() {
        List<Batch> allBatches = batchRepository.findAll();
        for (Batch b : allBatches) {
            Medicine m = medicineRepository.findById(b.getMedicineId());
            if (m != null) {
                if (b.getQuantityInStock() < m.getReorderThreshold()) {
                    if (!alertRepository.existsActiveAlert("LOW_STOCK", m.getMedicineId(), b.getBatchNo())) {
                        createAlert("LOW_STOCK", "Low stock for " + m.getMedicineName() + " in batch " + b.getBatchNo(), m.getMedicineId(), b.getBatchNo());
                    }
                }
                if (b.getExpiryDate().minusDays(30).isBefore(LocalDate.now())) {
                    if (!alertRepository.existsActiveAlert("EXPIRING_SOON", m.getMedicineId(), b.getBatchNo())) {
                        createAlert("EXPIRING_SOON", "Batch " + b.getBatchNo() + " for " + m.getMedicineName() + " is expiring soon.", m.getMedicineId(), b.getBatchNo());
                    }
                }
            }
        }
    }

    private void createAlert(String type, String message, Integer medicineId, String batchNo) {
        Alert alert = new Alert();
        alert.setAlertType(type);
        alert.setMessage(message);
        alert.setMedicineId(medicineId);
        alert.setBatchNo(batchNo);
        alertRepository.save(alert);
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findActiveAlerts();
    }

    public List<Alert> getResolvedAlerts() {
        return alertRepository.findResolvedAlerts();
    }

    public void resolveAlert(Integer alertId, Integer staffId) {
        alertRepository.resolve(alertId, staffId);
    }

    public List<Medicine> searchAllMedicinesWithStock(String keyword) {
        return medicineRepository.searchAllWithStock(keyword);
    }

    public void saveSupplier(com.abhapharmacy.model.Supplier supplier) {
        supplierRepository.save(supplier);
    }

    public void saveSupplier(com.abhapharmacy.model.Supplier supplier, String phone) {
        supplierRepository.save(supplier, phone);
    }

    public List<com.abhapharmacy.model.Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<java.util.Map<String, Object>> getAllSuppliersWithDetails() {
        return supplierRepository.findAllWithDetails();
    }
}
