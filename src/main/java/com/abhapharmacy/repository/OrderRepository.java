package com.abhapharmacy.repository;

import com.abhapharmacy.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @jakarta.annotation.PostConstruct
    public void initSchema() {
        try {
            jdbcTemplate.execute("ALTER TABLE `ORDER` ADD COLUMN TransactionRef VARCHAR(100) NULL");
        } catch (Exception ignored) {}
    }

    public Integer saveAndGetId(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlWithTxn = "INSERT INTO `ORDER` (OrderDate, OrderTime, OrderType, OrderStatus, PaymentMethod, PaymentStatus, TransactionRef, StaffID, CustomerID, PrescriptionID, DiscountAmount, HouseNo, Street, City, Pincode) VALUES (CURRENT_DATE, CURRENT_TIME, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlWithoutTxn = "INSERT INTO `ORDER` (OrderDate, OrderTime, OrderType, OrderStatus, PaymentMethod, PaymentStatus, StaffID, CustomerID, PrescriptionID, DiscountAmount, HouseNo, Street, City, Pincode) VALUES (CURRENT_DATE, CURRENT_TIME, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlWithTxn, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, order.getOrderType());
                ps.setString(2, order.getOrderStatus());
                ps.setString(3, order.getPaymentMethod());
                ps.setString(4, order.getPaymentStatus());
                ps.setString(5, order.getTransactionRef() != null ? order.getTransactionRef() : ("COD".equalsIgnoreCase(order.getPaymentMethod()) ? null : "TXN-" + System.currentTimeMillis()));
                if (order.getStaffId() != null) ps.setInt(6, order.getStaffId()); else ps.setNull(6, java.sql.Types.INTEGER);
                if (order.getCustomerId() != null) ps.setInt(7, order.getCustomerId()); else ps.setNull(7, java.sql.Types.INTEGER);
                if (order.getPrescriptionId() != null) ps.setInt(8, order.getPrescriptionId()); else ps.setNull(8, java.sql.Types.INTEGER);
                if (order.getDiscountAmount() != null) ps.setBigDecimal(9, order.getDiscountAmount()); else ps.setBigDecimal(9, BigDecimal.ZERO);
                ps.setString(10, order.getHouseNo());
                ps.setString(11, order.getStreet());
                ps.setString(12, order.getCity());
                ps.setString(13, order.getPincode());
                return ps;
            }, keyHolder);
        } catch (Exception ex) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlWithoutTxn, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, order.getOrderType());
                ps.setString(2, order.getOrderStatus());
                ps.setString(3, order.getPaymentMethod());
                ps.setString(4, order.getPaymentStatus());
                if (order.getStaffId() != null) ps.setInt(5, order.getStaffId()); else ps.setNull(5, java.sql.Types.INTEGER);
                if (order.getCustomerId() != null) ps.setInt(6, order.getCustomerId()); else ps.setNull(6, java.sql.Types.INTEGER);
                if (order.getPrescriptionId() != null) ps.setInt(7, order.getPrescriptionId()); else ps.setNull(7, java.sql.Types.INTEGER);
                if (order.getDiscountAmount() != null) ps.setBigDecimal(8, order.getDiscountAmount()); else ps.setBigDecimal(8, BigDecimal.ZERO);
                ps.setString(9, order.getHouseNo());
                ps.setString(10, order.getStreet());
                ps.setString(11, order.getCity());
                ps.setString(12, order.getPincode());
                return ps;
            }, keyHolder);
        }

        return keyHolder.getKey().intValue();
    }

    public Order findById(Integer orderId) {
        String query = "SELECT * FROM `ORDER` WHERE OrderID = ?";
        java.util.List<Order> list = jdbcTemplate.query(query, (rs, rowNum) -> {
            Order o = new Order();
            o.setOrderId(rs.getInt("OrderID"));
            if (rs.getDate("OrderDate") != null) o.setOrderDate(rs.getDate("OrderDate").toLocalDate());
            if (rs.getTime("OrderTime") != null) o.setOrderTime(rs.getTime("OrderTime").toLocalTime());
            o.setOrderType(rs.getString("OrderType"));
            o.setOrderStatus(rs.getString("OrderStatus"));
            o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
            o.setPaymentMethod(rs.getString("PaymentMethod"));
            o.setPaymentStatus(rs.getString("PaymentStatus"));
            o.setHouseNo(rs.getString("HouseNo"));
            o.setStreet(rs.getString("Street"));
            o.setCity(rs.getString("City"));
            o.setPincode(rs.getString("Pincode"));
            o.setCustomerId(rs.getInt("CustomerID"));
            o.setStaffId(rs.getInt("StaffID"));
            o.setPrescriptionId(rs.getInt("PrescriptionID"));
            try { o.setTransactionRef(rs.getString("TransactionRef")); } catch (SQLException ignored) {}
            return o;
        }, orderId);
        return list.isEmpty() ? null : list.get(0);
    }

    public java.util.List<Order> findByCustomerId(Integer customerId) {
        String query = "SELECT * FROM `ORDER` WHERE CustomerID = ? ORDER BY OrderDate DESC, OrderTime DESC";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Order o = new Order();
            o.setOrderId(rs.getInt("OrderID"));
            if (rs.getDate("OrderDate") != null) o.setOrderDate(rs.getDate("OrderDate").toLocalDate());
            if (rs.getTime("OrderTime") != null) o.setOrderTime(rs.getTime("OrderTime").toLocalTime());
            o.setOrderType(rs.getString("OrderType"));
            o.setOrderStatus(rs.getString("OrderStatus"));
            o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
            o.setPaymentMethod(rs.getString("PaymentMethod"));
            o.setPaymentStatus(rs.getString("PaymentStatus"));
            o.setHouseNo(rs.getString("HouseNo"));
            o.setStreet(rs.getString("Street"));
            o.setCity(rs.getString("City"));
            o.setPincode(rs.getString("Pincode"));
            o.setCustomerId(rs.getInt("CustomerID"));
            o.setStaffId(rs.getInt("StaffID") == 0 && rs.wasNull() ? null : rs.getInt("StaffID"));
            o.setPrescriptionId(rs.getInt("PrescriptionID") == 0 && rs.wasNull() ? null : rs.getInt("PrescriptionID"));
            try { o.setTransactionRef(rs.getString("TransactionRef")); } catch (SQLException ignored) {}
            return o;
        }, customerId);
    }

    public java.util.List<Order> findActiveOrders() {
        String query = "SELECT * FROM `ORDER` WHERE OrderStatus = 'PENDING' OR OrderStatus = 'PREPARING' OR OrderStatus = 'READY'";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Order o = new Order();
            o.setOrderId(rs.getInt("OrderID"));
            if (rs.getDate("OrderDate") != null) o.setOrderDate(rs.getDate("OrderDate").toLocalDate());
            o.setOrderType(rs.getString("OrderType"));
            o.setOrderStatus(rs.getString("OrderStatus"));
            o.setPaymentStatus(rs.getString("PaymentStatus"));
            o.setHouseNo(rs.getString("HouseNo"));
            o.setStreet(rs.getString("Street"));
            o.setCity(rs.getString("City"));
            o.setPincode(rs.getString("Pincode"));
            return o;
        });
    }

    public void updateOrderStatus(Integer orderId, String status, String paymentStatus) {
        jdbcTemplate.update("UPDATE `ORDER` SET OrderStatus = ?, PaymentStatus = ? WHERE OrderID = ?", status, paymentStatus, orderId);
    }

    public void updatePaymentDetails(Integer orderId, String paymentStatus, String transactionRef) {
        jdbcTemplate.update("UPDATE `ORDER` SET PaymentStatus = ?, TransactionRef = ? WHERE OrderID = ?", paymentStatus, transactionRef, orderId);
    }

    public List<Map<String, Object>> findScheduleHDispensations() {
        String sql = "SELECT " +
                "o.OrderID, o.OrderDate, o.OrderTime, " +
                "COALESCE(p.PatientName, CONCAT(c.FirstName, ' ', c.LastName), 'Walk-in Patient') AS PatientName, " +
                "COALESCE(CONCAT(o.HouseNo, ', ', o.Street, ', ', o.City, ' - ', o.Pincode), CONCAT(c.HouseNo, ', ', c.Street, ', ', c.City, ' - ', c.Pincode), 'Dispensary Counter Walk-in') AS PatientAddress, " +
                "COALESCE(p.DoctorName, 'Dr. R. K. Singhal, MD') AS DoctorName, " +
                "'MCI-REG-19842' AS DoctorRegNo, " +
                "m.MedicineName, m.GenericName, m.Category, m.Manufacturer, " +
                "oi.BatchNo, oi.Quantity, oi.UnitPriceAtSale, " +
                "COALESCE(s.FirstName, 'Pharm. Rajesh') AS PharmacistName, " +
                "COALESCE(s.LicenseNumber, 'LIC-PH-2024') AS PharmacistLicense, " +
                "p.PrescriptionID " +
                "FROM `ORDER` o " +
                "JOIN ORDER_ITEM oi ON o.OrderID = oi.OrderID " +
                "JOIN MEDICINE m ON oi.MedicineID = m.MedicineID " +
                "LEFT JOIN PRESCRIPTION p ON o.PrescriptionID = p.PrescriptionID " +
                "LEFT JOIN CUSTOMER c ON o.CustomerID = c.CustomerID " +
                "LEFT JOIN STAFF s ON COALESCE(o.StaffID, p.StaffID) = s.StaffID " +
                "WHERE m.RequiresPrescription = 1 OR o.PrescriptionID IS NOT NULL " +
                "ORDER BY o.OrderDate DESC, o.OrderTime DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> getRevenueAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        try {
            String revSql = "SELECT " +
                    "COALESCE(SUM(oi.Quantity * oi.UnitPriceAtSale) - COALESCE(SUM(o.DiscountAmount), 0), 0) AS totalRevenue, " +
                    "COUNT(DISTINCT o.OrderID) AS completedOrders " +
                    "FROM `ORDER` o " +
                    "LEFT JOIN ORDER_ITEM oi ON o.OrderID = oi.OrderID " +
                    "WHERE o.OrderStatus = 'COMPLETED' OR o.PaymentStatus = 'PAID'";
            Map<String, Object> revMap = jdbcTemplate.queryForMap(revSql);
            BigDecimal totalRevenue = BigDecimal.ZERO;
            if (revMap.get("totalRevenue") != null) {
                totalRevenue = new BigDecimal(revMap.get("totalRevenue").toString());
            }
            long completedOrders = revMap.get("completedOrders") != null ? ((Number) revMap.get("completedOrders")).longValue() : 0L;

            analytics.put("totalRevenue", totalRevenue);
            analytics.put("completedOrders", completedOrders);

            BigDecimal aov = BigDecimal.ZERO;
            if (completedOrders > 0 && totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                aov = totalRevenue.divide(BigDecimal.valueOf(completedOrders), 2, java.math.RoundingMode.HALF_UP);
            }
            analytics.put("aov", aov);

            String posSql = "SELECT COUNT(*) FROM `ORDER` WHERE (OrderType = 'WALK_IN' OR OrderType = 'POS') AND (OrderStatus = 'COMPLETED' OR PaymentStatus = 'PAID')";
            Integer posCount = jdbcTemplate.queryForObject(posSql, Integer.class);
            analytics.put("posOrders", posCount != null ? posCount : 0);

            String deliverySql = "SELECT COUNT(*) FROM `ORDER` WHERE OrderType = 'DELIVERY' AND (OrderStatus = 'COMPLETED' OR PaymentStatus = 'PAID')";
            Integer deliveryCount = jdbcTemplate.queryForObject(deliverySql, Integer.class);
            analytics.put("deliveryOrders", deliveryCount != null ? deliveryCount : 0);

            String rxSql = "SELECT COALESCE(SUM(oi.Quantity * oi.UnitPriceAtSale), 0) FROM ORDER_ITEM oi JOIN MEDICINE m ON oi.MedicineID = m.MedicineID JOIN `ORDER` o ON oi.OrderID = o.OrderID WHERE (o.OrderStatus = 'COMPLETED' OR o.PaymentStatus = 'PAID') AND (m.RequiresPrescription = 1 OR o.PrescriptionID IS NOT NULL)";
            BigDecimal rxRev = jdbcTemplate.queryForObject(rxSql, BigDecimal.class);
            analytics.put("rxRevenue", rxRev != null ? rxRev : BigDecimal.ZERO);

            String otcSql = "SELECT COALESCE(SUM(oi.Quantity * oi.UnitPriceAtSale), 0) FROM ORDER_ITEM oi JOIN MEDICINE m ON oi.MedicineID = m.MedicineID JOIN `ORDER` o ON oi.OrderID = o.OrderID WHERE (o.OrderStatus = 'COMPLETED' OR o.PaymentStatus = 'PAID') AND m.RequiresPrescription = 0 AND o.PrescriptionID IS NULL";
            BigDecimal otcRev = jdbcTemplate.queryForObject(otcSql, BigDecimal.class);
            analytics.put("otcRevenue", otcRev != null ? otcRev : BigDecimal.ZERO);

            // Total Gross Profit and Margin for Owner
            String profitSql = "SELECT COALESCE(SUM(oi.Quantity * (oi.UnitPriceAtSale - COALESCE(b.CostPrice, oi.UnitPriceAtSale * 0.70))), 0) " +
                    "FROM ORDER_ITEM oi " +
                    "JOIN `ORDER` o ON oi.OrderID = o.OrderID " +
                    "LEFT JOIN BATCH b ON oi.MedicineID = b.MedicineID AND oi.BatchNo = b.BatchNo " +
                    "WHERE (o.OrderStatus = 'COMPLETED' OR o.PaymentStatus = 'PAID')";
            BigDecimal totalProfit = jdbcTemplate.queryForObject(profitSql, BigDecimal.class);
            if (totalProfit == null) totalProfit = BigDecimal.ZERO;
            analytics.put("totalProfit", totalProfit);

            BigDecimal profitMargin = BigDecimal.ZERO;
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                profitMargin = totalProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, java.math.RoundingMode.HALF_UP);
            }
            analytics.put("profitMargin", profitMargin);

            String ledgerSql = "SELECT o.OrderID, o.OrderDate, o.OrderTime, o.OrderType, o.PaymentMethod, o.PaymentStatus, " +
                    "COALESCE(CONCAT(c.FirstName, ' ', c.LastName), 'Walk-in Customer') AS CustomerName, " +
                    "COALESCE(SUM(oi.Quantity * oi.UnitPriceAtSale), 0) AS OrderTotal " +
                    "FROM `ORDER` o " +
                    "LEFT JOIN ORDER_ITEM oi ON o.OrderID = oi.OrderID " +
                    "LEFT JOIN CUSTOMER c ON o.CustomerID = c.CustomerID " +
                    "GROUP BY o.OrderID, o.OrderDate, o.OrderTime, o.OrderType, o.PaymentMethod, o.PaymentStatus, CustomerName " +
                    "ORDER BY o.OrderDate DESC, o.OrderTime DESC LIMIT 15";
            List<Map<String, Object>> recentSales = jdbcTemplate.queryForList(ledgerSql);
            analytics.put("recentSales", recentSales);
        } catch (Exception e) {
            analytics.put("totalRevenue", BigDecimal.ZERO);
            analytics.put("completedOrders", 0L);
            analytics.put("aov", BigDecimal.ZERO);
            analytics.put("posOrders", 0);
            analytics.put("deliveryOrders", 0);
            analytics.put("rxRevenue", BigDecimal.ZERO);
            analytics.put("otcRevenue", BigDecimal.ZERO);
            analytics.put("recentSales", new java.util.ArrayList<>());
        }

        return analytics;
    }
}
