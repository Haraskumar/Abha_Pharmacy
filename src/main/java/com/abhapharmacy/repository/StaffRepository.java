package com.abhapharmacy.repository;

import com.abhapharmacy.model.Staff;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StaffRepository {

    private final JdbcTemplate jdbcTemplate;

    public StaffRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Staff> rowMapper = new RowMapper<Staff>() {
        @Override
        public Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
            Staff s = new Staff();
            s.setStaffId(rs.getInt("StaffID"));
            s.setFirstName(rs.getString("FirstName"));
            s.setLastName(rs.getString("LastName"));
            s.setEmail(rs.getString("Email"));
            s.setPasswordHash(rs.getString("PasswordHash"));
            s.setRole(rs.getString("Role"));
            s.setLicenseNumber(rs.getString("LicenseNumber"));
            if (rs.getDate("HireDate") != null) {
                s.setHireDate(rs.getDate("HireDate").toLocalDate());
            }
            s.setSalary(rs.getBigDecimal("Salary"));
            return s;
        }
    };

    public Staff findByEmail(String email) {
        List<Staff> list = jdbcTemplate.query("SELECT * FROM STAFF WHERE Email = ?", rowMapper, email);
        return list.isEmpty() ? null : list.get(0);
    }

    public Staff findById(Integer id) {
        List<Staff> list = jdbcTemplate.query("SELECT * FROM STAFF WHERE StaffID = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Staff> findAll() {
        return jdbcTemplate.query("SELECT * FROM STAFF", rowMapper);
    }

    public void save(Staff s) {
        jdbcTemplate.update("INSERT INTO STAFF (FirstName, LastName, Email, PasswordHash, Role, LicenseNumber, HireDate, Salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                s.getFirstName(), s.getLastName(), s.getEmail(), s.getPasswordHash(), s.getRole(), s.getLicenseNumber(), s.getHireDate(), s.getSalary());
    }

    public void updateRole(Integer staffId, String role) {
        jdbcTemplate.update("UPDATE STAFF SET Role = ? WHERE StaffID = ?", role, staffId);
    }

    public void updateStaffDetails(Integer staffId, String firstName, String lastName, String role, String licenseNumber, java.math.BigDecimal salary, String newPasswordHash) {
        if (newPasswordHash != null && !newPasswordHash.trim().isEmpty()) {
            jdbcTemplate.update("UPDATE STAFF SET FirstName = ?, LastName = ?, Role = ?, LicenseNumber = ?, Salary = ?, PasswordHash = ? WHERE StaffID = ?",
                    firstName, lastName, role, licenseNumber, salary, newPasswordHash, staffId);
        } else {
            jdbcTemplate.update("UPDATE STAFF SET FirstName = ?, LastName = ?, Role = ?, LicenseNumber = ?, Salary = ? WHERE StaffID = ?",
                    firstName, lastName, role, licenseNumber, salary, staffId);
        }
    }
}
