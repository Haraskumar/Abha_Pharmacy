package com.abhapharmacy.repository;

import com.abhapharmacy.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Customer> rowMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer c = new Customer();
            c.setCustomerId(rs.getInt("CustomerID"));
            c.setFirstName(rs.getString("FirstName"));
            c.setLastName(rs.getString("LastName"));
            c.setHouseNo(rs.getString("HouseNo"));
            c.setStreet(rs.getString("Street"));
            c.setCity(rs.getString("City"));
            c.setPincode(rs.getString("Pincode"));
            c.setEmail(rs.getString("Email"));
            c.setPasswordHash(rs.getString("PasswordHash"));
            if (rs.getDate("DateOfBirth") != null) {
                c.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());
            }
            c.setGender(rs.getString("Gender"));
            if (rs.getDate("RegistrationDate") != null) {
                c.setRegistrationDate(rs.getDate("RegistrationDate").toLocalDate());
            }
            return c;
        }
    };

    public Customer findByEmail(String email) {
        List<Customer> list = jdbcTemplate.query("SELECT * FROM CUSTOMER WHERE Email = ?", rowMapper, email);
        return list.isEmpty() ? null : list.get(0);
    }

    public Customer findById(Integer id) {
        List<Customer> list = jdbcTemplate.query("SELECT * FROM CUSTOMER WHERE CustomerID = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Integer saveAndGetId(Customer c) {
        org.springframework.jdbc.support.KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO CUSTOMER (FirstName, LastName, HouseNo, Street, City, Pincode, Email, PasswordHash, DateOfBirth, Gender, RegistrationDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)",
                java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getHouseNo());
            ps.setString(4, c.getStreet());
            ps.setString(5, c.getCity());
            ps.setString(6, c.getPincode());
            ps.setString(7, c.getEmail());
            ps.setString(8, c.getPasswordHash());
            ps.setObject(9, c.getDateOfBirth());
            ps.setString(10, c.getGender());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void savePhone(Integer customerId, String phone) {
        jdbcTemplate.update("INSERT INTO CUSTOMER_PHONE (CustomerID, PhoneNumber) VALUES (?, ?)", customerId, phone);
    }

    public List<String> findPhonesByCustomerId(Integer customerId) {
        return jdbcTemplate.queryForList("SELECT PhoneNumber FROM CUSTOMER_PHONE WHERE CustomerID = ?", String.class, customerId);
    }

    public void updateProfile(Customer c) {
        jdbcTemplate.update(
            "UPDATE CUSTOMER SET FirstName = ?, LastName = ?, HouseNo = ?, Street = ?, City = ?, Pincode = ? WHERE CustomerID = ?",
            c.getFirstName(), c.getLastName(), c.getHouseNo(), c.getStreet(), c.getCity(), c.getPincode(), c.getCustomerId()
        );
    }

    public void updatePhone(Integer customerId, String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            jdbcTemplate.update("DELETE FROM CUSTOMER_PHONE WHERE CustomerID = ?", customerId);
            jdbcTemplate.update("INSERT INTO CUSTOMER_PHONE (CustomerID, PhoneNumber) VALUES (?, ?)", customerId, phone.trim());
        }
    }

    public List<Customer> findAll() {
        return jdbcTemplate.query("SELECT * FROM CUSTOMER ORDER BY CustomerID DESC", rowMapper);
    }

    public Customer findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null;
        List<Customer> list = jdbcTemplate.query(
            "SELECT c.* FROM CUSTOMER c JOIN CUSTOMER_PHONE cp ON c.CustomerID = cp.CustomerID WHERE cp.PhoneNumber = ? LIMIT 1",
            rowMapper, phone.trim()
        );
        return list.isEmpty() ? null : list.get(0);
    }
}
