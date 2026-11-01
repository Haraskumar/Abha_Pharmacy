package com.abhapharmacy.repository;

import com.abhapharmacy.model.Alert;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AlertRepository {

    private final JdbcTemplate jdbcTemplate;

    public AlertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Alert> rowMapper = new RowMapper<Alert>() {
        @Override
        public Alert mapRow(ResultSet rs, int rowNum) throws SQLException {
            Alert a = new Alert();
            a.setAlertId(rs.getInt("AlertID"));
            a.setAlertType(rs.getString("AlertType"));
            a.setMessage(rs.getString("Message"));
            a.setStatus(rs.getString("Status"));
            if (rs.getTimestamp("CreatedAt") != null) {
                a.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
            }
            if (rs.getTimestamp("ResolvedAt") != null) {
                a.setResolvedAt(rs.getTimestamp("ResolvedAt").toLocalDateTime());
            }
            a.setMedicineId(rs.getInt("MedicineID") == 0 && rs.wasNull() ? null : rs.getInt("MedicineID"));
            a.setBatchNo(rs.getString("BatchNo"));
            a.setStaffId(rs.getInt("StaffID") == 0 && rs.wasNull() ? null : rs.getInt("StaffID"));
            if (rs.getDate("ResolvedDate") != null) {
                a.setResolvedDate(rs.getDate("ResolvedDate").toLocalDate());
            }
            a.setActionTaken(rs.getString("ActionTaken"));

            try {
                a.setMedicineName(rs.getString("MedicineName"));
                a.setGenericName(rs.getString("GenericName"));
                a.setCategory(rs.getString("Category"));
                a.setReorderThreshold(rs.getInt("ReorderThreshold") == 0 && rs.wasNull() ? null : rs.getInt("ReorderThreshold"));
                a.setQuantityInStock(rs.getInt("QuantityInStock") == 0 && rs.wasNull() ? null : rs.getInt("QuantityInStock"));
                if (rs.getDate("ExpiryDate") != null) {
                    a.setExpiryDate(rs.getDate("ExpiryDate").toLocalDate());
                }
            } catch (SQLException ignored) {}

            try {
                a.setResolvedByStaffName(rs.getString("ResolvedByStaff"));
            } catch (SQLException ignored) {}

            return a;
        }
    };

    public List<Alert> findActiveAlerts() {
        String sql = "SELECT a.*, m.MedicineName, m.GenericName, m.Category, m.ReorderThreshold, " +
                     "b.QuantityInStock, b.ExpiryDate " +
                     "FROM ALERT a " +
                     "LEFT JOIN MEDICINE m ON a.MedicineID = m.MedicineID " +
                     "LEFT JOIN BATCH b ON a.MedicineID = b.MedicineID AND a.BatchNo = b.BatchNo " +
                     "WHERE a.Status = 'ACTIVE' " +
                     "ORDER BY a.CreatedAt DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Alert> findResolvedAlerts() {
        String sql = "SELECT a.*, m.MedicineName, m.GenericName, m.Category, m.ReorderThreshold, " +
                     "b.QuantityInStock, b.ExpiryDate, " +
                     "CONCAT(s.FirstName, ' ', s.LastName) AS ResolvedByStaff " +
                     "FROM ALERT a " +
                     "LEFT JOIN MEDICINE m ON a.MedicineID = m.MedicineID " +
                     "LEFT JOIN BATCH b ON a.MedicineID = b.MedicineID AND a.BatchNo = b.BatchNo " +
                     "LEFT JOIN STAFF s ON a.StaffID = s.StaffID " +
                     "WHERE a.Status = 'RESOLVED' " +
                     "ORDER BY a.ResolvedAt DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void save(Alert alert) {
        jdbcTemplate.update("INSERT INTO ALERT (AlertType, Message, Status, MedicineID, BatchNo) VALUES (?, ?, 'ACTIVE', ?, ?)",
                alert.getAlertType(), alert.getMessage(), alert.getMedicineId(), alert.getBatchNo());
    }

    public void resolve(Integer alertId, Integer staffId) {
        jdbcTemplate.update("UPDATE ALERT SET Status = 'RESOLVED', ResolvedAt = CURRENT_TIMESTAMP, StaffID = ?, ResolvedDate = CURRENT_DATE, ActionTaken = 'Acknowledged by staff' WHERE AlertID = ?",
                staffId, alertId);
    }

    public boolean existsActiveAlert(String type, Integer medicineId, String batchNo) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM ALERT WHERE AlertType = ? AND MedicineID = ? AND BatchNo = ? AND Status = 'ACTIVE'",
            Integer.class, type, medicineId, batchNo);
        return count != null && count > 0;
    }
}
