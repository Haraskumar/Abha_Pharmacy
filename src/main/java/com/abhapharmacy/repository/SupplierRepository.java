package com.abhapharmacy.repository;

import com.abhapharmacy.model.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierRepository {
    private final JdbcTemplate jdbcTemplate;

    public SupplierRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Supplier s) {
        save(s, null);
    }

    public void save(Supplier s, String phone) {
        org.springframework.jdbc.support.GeneratedKeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO SUPPLIER (SupplierName, ContactPerson, Email, GSTIN, Street, City, State, Pincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, s.getSupplierName());
            ps.setString(2, s.getContactPerson());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getGstin());
            ps.setString(5, s.getStreet());
            ps.setString(6, s.getCity());
            ps.setString(7, s.getState());
            ps.setString(8, s.getPincode());
            return ps;
        }, keyHolder);

        if (phone != null && !phone.trim().isEmpty() && keyHolder.getKey() != null) {
            int supplierId = keyHolder.getKey().intValue();
            try {
                jdbcTemplate.update("INSERT INTO SUPPLIER_PHONE (SupplierID, PhoneNumber) VALUES (?, ?)", supplierId, phone.trim());
            } catch (Exception ignored) {}
        }
    }

    public java.util.List<java.util.Map<String, Object>> findAllWithDetails() {
        String sql = "SELECT s.SupplierID, s.SupplierName, s.ContactPerson, s.Email, s.GSTIN, s.Street, s.City, s.State, s.Pincode, " +
                "(SELECT PhoneNumber FROM SUPPLIER_PHONE sp WHERE sp.SupplierID = s.SupplierID LIMIT 1) AS PhoneNumber, " +
                "COUNT(b.BatchNo) AS batchCount, " +
                "COALESCE(SUM(b.PurchaseAmount), 0) AS totalInwardValue " +
                "FROM SUPPLIER s " +
                "LEFT JOIN BATCH b ON s.SupplierID = b.SupplierID " +
                "GROUP BY s.SupplierID, s.SupplierName, s.ContactPerson, s.Email, s.GSTIN, s.Street, s.City, s.State, s.Pincode " +
                "ORDER BY s.SupplierID ASC";
        return jdbcTemplate.queryForList(sql);
    }

    public java.util.List<Supplier> findAll() {
        return jdbcTemplate.query("SELECT * FROM SUPPLIER", new org.springframework.jdbc.core.RowMapper<Supplier>() {
            @Override
            public Supplier mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("SupplierID"));
                s.setSupplierName(rs.getString("SupplierName"));
                s.setContactPerson(rs.getString("ContactPerson"));
                s.setEmail(rs.getString("Email"));
                s.setGstin(rs.getString("GSTIN"));
                s.setStreet(rs.getString("Street"));
                s.setCity(rs.getString("City"));
                s.setState(rs.getString("State"));
                s.setPincode(rs.getString("Pincode"));
                return s;
            }
        });
    }
}
