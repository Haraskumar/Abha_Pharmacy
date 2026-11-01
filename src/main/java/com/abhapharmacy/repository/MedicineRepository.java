package com.abhapharmacy.repository;

import com.abhapharmacy.model.Medicine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MedicineRepository {

    private final JdbcTemplate jdbcTemplate;

    public MedicineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Medicine> rowMapper = new RowMapper<Medicine>() {
        @Override
        public Medicine mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medicine m = new Medicine();
            m.setMedicineId(rs.getInt("MedicineID"));
            m.setMedicineName(rs.getString("MedicineName"));
            m.setGenericName(rs.getString("GenericName"));
            m.setManufacturer(rs.getString("Manufacturer"));
            m.setCategory(rs.getString("Category"));
            m.setRequiresPrescription(rs.getBoolean("RequiresPrescription"));
            m.setReorderThreshold(rs.getInt("ReorderThreshold"));
            m.setStorageCondition(rs.getString("StorageCondition"));
            m.setDescription(rs.getString("Description"));
            return m;
        }
    };

    public List<Medicine> findAll() {
        return jdbcTemplate.query("SELECT m.*, COALESCE(SUM(CASE WHEN b.ExpiryDate >= CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) as TotalStock, COALESCE(SUM(b.QuantityInStock), 0) as GrossStock, (SELECT SellingPrice FROM BATCH WHERE MedicineID = m.MedicineID ORDER BY SupplyDate DESC, ExpiryDate DESC LIMIT 1) as LatestPrice FROM MEDICINE m LEFT JOIN BATCH b ON m.MedicineID = b.MedicineID GROUP BY m.MedicineID", rowMapperWithStock);
    }

    public void save(Medicine m) {
        jdbcTemplate.update("INSERT INTO MEDICINE (MedicineName, GenericName, Manufacturer, Category, RequiresPrescription, ReorderThreshold, StorageCondition, Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                m.getMedicineName(), m.getGenericName(), m.getManufacturer(), m.getCategory(), m.getRequiresPrescription(), m.getReorderThreshold(), m.getStorageCondition(), m.getDescription());
    }

    public List<Medicine> searchByName(String keyword) {
        String query = "SELECT * FROM MEDICINE WHERE MedicineName LIKE ? OR GenericName LIKE ?";
        String param = "%" + keyword + "%";
        return jdbcTemplate.query(query, rowMapper, param, param);
    }

    private RowMapper<Medicine> rowMapperWithStock = new RowMapper<Medicine>() {
        @Override
        public Medicine mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medicine m = rowMapper.mapRow(rs, rowNum);
            m.setTotalStock(rs.getInt("TotalStock"));
            m.setLatestPrice(rs.getBigDecimal("LatestPrice"));
            m.setExpired(rs.getInt("GrossStock") > 0 && rs.getInt("TotalStock") == 0);
            return m;
        }
    };

    public List<Medicine> findAllOTC() {
        return jdbcTemplate.query("SELECT m.*, COALESCE(SUM(CASE WHEN b.ExpiryDate >= CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) as TotalStock, COALESCE(SUM(b.QuantityInStock), 0) as GrossStock, (SELECT SellingPrice FROM BATCH WHERE MedicineID = m.MedicineID ORDER BY SupplyDate DESC, ExpiryDate DESC LIMIT 1) as LatestPrice FROM MEDICINE m LEFT JOIN BATCH b ON m.MedicineID = b.MedicineID WHERE m.RequiresPrescription = FALSE GROUP BY m.MedicineID", rowMapperWithStock);
    }

    public List<Medicine> searchOTCByName(String keyword) {
        String query = "SELECT m.*, COALESCE(SUM(CASE WHEN b.ExpiryDate >= CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) as TotalStock, COALESCE(SUM(b.QuantityInStock), 0) as GrossStock, (SELECT SellingPrice FROM BATCH WHERE MedicineID = m.MedicineID ORDER BY SupplyDate DESC, ExpiryDate DESC LIMIT 1) as LatestPrice FROM MEDICINE m LEFT JOIN BATCH b ON m.MedicineID = b.MedicineID WHERE m.RequiresPrescription = FALSE AND (m.MedicineName LIKE ? OR m.GenericName LIKE ?) GROUP BY m.MedicineID";
        String param = "%" + keyword + "%";
        return jdbcTemplate.query(query, rowMapperWithStock, param, param);
    }

    public Medicine findById(Integer id) {
        String query = "SELECT m.*, COALESCE(SUM(CASE WHEN b.ExpiryDate >= CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) as TotalStock, COALESCE(SUM(b.QuantityInStock), 0) as GrossStock, (SELECT SellingPrice FROM BATCH WHERE MedicineID = m.MedicineID ORDER BY SupplyDate DESC, ExpiryDate DESC LIMIT 1) as LatestPrice FROM MEDICINE m LEFT JOIN BATCH b ON m.MedicineID = b.MedicineID WHERE m.MedicineID = ? GROUP BY m.MedicineID";
        List<Medicine> list = jdbcTemplate.query(query, rowMapperWithStock, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Medicine> searchAllWithStock(String keyword) {
        String query = "SELECT m.*, COALESCE(SUM(CASE WHEN b.ExpiryDate >= CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) as TotalStock, COALESCE(SUM(b.QuantityInStock), 0) as GrossStock, (SELECT SellingPrice FROM BATCH WHERE MedicineID = m.MedicineID ORDER BY SupplyDate DESC, ExpiryDate DESC LIMIT 1) as LatestPrice FROM MEDICINE m LEFT JOIN BATCH b ON m.MedicineID = b.MedicineID WHERE (m.MedicineName LIKE ? OR m.GenericName LIKE ?) GROUP BY m.MedicineID";
        String param = "%" + keyword + "%";
        return jdbcTemplate.query(query, rowMapperWithStock, param, param);
    }
}
