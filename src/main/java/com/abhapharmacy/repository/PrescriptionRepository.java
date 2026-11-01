package com.abhapharmacy.repository;

import com.abhapharmacy.model.Prescription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PrescriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PrescriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Prescription> rowMapper = new RowMapper<Prescription>() {
        @Override
        public Prescription mapRow(ResultSet rs, int rowNum) throws SQLException {
            Prescription p = new Prescription();
            p.setPrescriptionId(rs.getInt("PrescriptionID"));
            p.setPrescriptionImageUrl(rs.getString("PrescriptionImageURL"));
            if (rs.getDate("UploadDate") != null) {
                p.setUploadDate(rs.getDate("UploadDate").toLocalDate());
            }
            if (rs.getTime("UploadTime") != null) {
                p.setUploadTime(rs.getTime("UploadTime").toLocalTime());
            }
            p.setDoctorName(rs.getString("DoctorName"));
            p.setPatientName(rs.getString("PatientName"));
            p.setVerificationStatus(rs.getString("VerificationStatus"));
            p.setPharmacistRemarks(rs.getString("PharmacistRemarks"));
            p.setInstructions(rs.getString("Instructions"));
            p.setCustomerId(rs.getInt("CustomerID"));
            p.setStaffId(rs.getInt("StaffID") == 0 && rs.wasNull() ? null : rs.getInt("StaffID"));
            if (rs.getDate("VerificationDate") != null) {
                p.setVerificationDate(rs.getDate("VerificationDate").toLocalDate());
            }
            if (rs.getTime("VerificationTime") != null) {
                p.setVerificationTime(rs.getTime("VerificationTime").toLocalTime());
            }
            return p;
        }
    };

    public void save(Prescription p) {
        jdbcTemplate.update("INSERT INTO PRESCRIPTION (PrescriptionImageURL, UploadDate, UploadTime, VerificationStatus, Instructions, CustomerID) VALUES (?, CURRENT_DATE, CURRENT_TIME, 'PENDING', ?, ?)",
                p.getPrescriptionImageUrl(), p.getInstructions(), p.getCustomerId());
    }

    public List<Prescription> findPending() {
        return jdbcTemplate.query("SELECT * FROM PRESCRIPTION WHERE VerificationStatus = 'PENDING'", rowMapper);
    }

    public List<Prescription> findByCustomerId(Integer customerId) {
        return jdbcTemplate.query("SELECT * FROM PRESCRIPTION WHERE CustomerID = ?", rowMapper, customerId);
    }

    public void updateStatus(Integer prescriptionId, String status, Integer staffId) {
        jdbcTemplate.update("UPDATE PRESCRIPTION SET VerificationStatus = ?, StaffID = ?, VerificationDate = CURRENT_DATE, VerificationTime = CURRENT_TIME WHERE PrescriptionID = ?",
                status, staffId, prescriptionId);
    }

    public Prescription findById(Integer prescriptionId) {
        List<Prescription> list = jdbcTemplate.query("SELECT * FROM PRESCRIPTION WHERE PrescriptionID = ?", rowMapper, prescriptionId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Prescription> findRecentProcessed() {
        return jdbcTemplate.query("SELECT * FROM PRESCRIPTION WHERE VerificationStatus != 'PENDING' ORDER BY VerificationDate DESC, VerificationTime DESC, PrescriptionID DESC LIMIT 15", rowMapper);
    }
}
