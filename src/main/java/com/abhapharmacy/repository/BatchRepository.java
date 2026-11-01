package com.abhapharmacy.repository;

import com.abhapharmacy.model.Batch;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public BatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Batch> rowMapper = new RowMapper<Batch>() {
        @Override
        public Batch mapRow(ResultSet rs, int rowNum) throws SQLException {
            Batch b = new Batch();
            b.setMedicineId(rs.getInt("MedicineID"));
            b.setBatchNo(rs.getString("BatchNo"));
            if (rs.getDate("MfgDate") != null) {
                b.setMfgDate(rs.getDate("MfgDate").toLocalDate());
            }
            if (rs.getDate("ExpiryDate") != null) {
                b.setExpiryDate(rs.getDate("ExpiryDate").toLocalDate());
            }
            b.setQuantityInStock(rs.getInt("QuantityInStock"));
            b.setCostPrice(rs.getBigDecimal("CostPrice"));
            b.setSellingPrice(rs.getBigDecimal("SellingPrice"));
            b.setRackLocation(rs.getString("RackLocation"));
            b.setSupplierId(rs.getInt("SupplierID"));
            if (rs.getDate("SupplyDate") != null) {
                b.setSupplyDate(rs.getDate("SupplyDate").toLocalDate());
            }
            b.setPurchaseAmount(rs.getBigDecimal("PurchaseAmount"));
            return b;
        }
    };

    public List<Batch> findByMedicineId(Integer medicineId) {
        return jdbcTemplate.query("SELECT * FROM BATCH WHERE MedicineID = ?", rowMapper, medicineId);
    }

    public int deductStock(Integer medicineId, String batchNo, Integer quantity) {
        return jdbcTemplate.update(
            "UPDATE BATCH SET QuantityInStock = QuantityInStock - ? WHERE MedicineID = ? AND BatchNo = ? AND QuantityInStock >= ?",
            quantity, medicineId, batchNo, quantity);
    }

    public List<Batch> findAll() {
        return jdbcTemplate.query("SELECT * FROM BATCH", rowMapper);
    }

    public void upsertBatch(Batch b) {
        jdbcTemplate.update(
            "INSERT INTO BATCH (MedicineID, BatchNo, MfgDate, ExpiryDate, QuantityInStock, CostPrice, SellingPrice, RackLocation, SupplierID, SupplyDate, PurchaseAmount) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE QuantityInStock = QuantityInStock + VALUES(QuantityInStock), PurchaseAmount = PurchaseAmount + VALUES(PurchaseAmount)",
            b.getMedicineId(), b.getBatchNo(), b.getMfgDate(), b.getExpiryDate(),
            b.getQuantityInStock(), b.getCostPrice(), b.getSellingPrice(),
            b.getRackLocation(), b.getSupplierId(), b.getSupplyDate(), b.getPurchaseAmount());
    }

    public Batch findByMedicineIdAndBatchNo(Integer medicineId, String batchNo) {
        List<Batch> list = jdbcTemplate.query("SELECT * FROM BATCH WHERE MedicineID = ? AND BatchNo = ?", rowMapper, medicineId, batchNo);
        return list.isEmpty() ? null : list.get(0);
    }
}
