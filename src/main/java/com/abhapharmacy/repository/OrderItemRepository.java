package com.abhapharmacy.repository;

import com.abhapharmacy.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(OrderItem item) {
        jdbcTemplate.update("INSERT INTO ORDER_ITEM (OrderID, ItemSeqNo, Quantity, UnitPriceAtSale, MedicineID, BatchNo) VALUES (?, ?, ?, ?, ?, ?)",
                item.getOrderId(), item.getItemSeqNo(), item.getQuantity(), item.getUnitPriceAtSale(), item.getMedicineId(), item.getBatchNo());
    }

    public java.util.List<OrderItem> findByOrderId(Integer orderId) {
        return jdbcTemplate.query("SELECT oi.*, m.MedicineName FROM ORDER_ITEM oi JOIN MEDICINE m ON oi.MedicineID = m.MedicineID WHERE oi.OrderID = ?", (rs, rowNum) -> {
            OrderItem i = new OrderItem();
            i.setOrderId(rs.getInt("OrderID"));
            i.setItemSeqNo(rs.getInt("ItemSeqNo"));
            i.setQuantity(rs.getInt("Quantity"));
            i.setUnitPriceAtSale(rs.getBigDecimal("UnitPriceAtSale"));
            i.setMedicineId(rs.getInt("MedicineID"));
            i.setBatchNo(rs.getString("BatchNo"));
            i.setMedicineName(rs.getString("MedicineName"));
            return i;
        }, orderId);
    }
}
