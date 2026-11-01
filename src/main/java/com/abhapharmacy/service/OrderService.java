package com.abhapharmacy.service;

import com.abhapharmacy.model.Batch;
import com.abhapharmacy.model.Order;
import com.abhapharmacy.model.OrderItem;
import com.abhapharmacy.repository.BatchRepository;
import com.abhapharmacy.repository.OrderItemRepository;
import com.abhapharmacy.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BatchRepository batchRepository;
    private final InventoryService inventoryService;
    private final com.abhapharmacy.repository.CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, BatchRepository batchRepository, InventoryService inventoryService, com.abhapharmacy.repository.CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.batchRepository = batchRepository;
        this.inventoryService = inventoryService;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void placeOtcOrder(Integer customerId, Integer medicineId) {
        com.abhapharmacy.model.Customer customer = customerRepository.findById(customerId);
        if (customer == null) throw new RuntimeException("Customer not found.");

        List<Batch> batches = batchRepository.findByMedicineId(medicineId);
        batches.sort((b1, b2) -> b1.getExpiryDate().compareTo(b2.getExpiryDate()));
        Batch activeBatch = null;
        for (Batch b : batches) {
            if (b.getQuantityInStock() > 0 && b.getExpiryDate().isAfter(LocalDate.now())) {
                activeBatch = b;
                break;
            }
        }
        if (activeBatch == null) {
            throw new RuntimeException("Medicine is currently out of stock or expired.");
        }

        Order order = new Order();
        order.setOrderType("DELIVERY");
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod("COD");
        order.setCustomerId(customerId);
        order.setHouseNo(customer.getHouseNo());
        order.setStreet(customer.getStreet());
        order.setCity(customer.getCity());
        order.setPincode(customer.getPincode());

        Integer orderId = orderRepository.saveAndGetId(order);

        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setItemSeqNo(1);
        item.setQuantity(1);
        item.setUnitPriceAtSale(activeBatch.getSellingPrice());
        item.setMedicineId(medicineId);
        item.setBatchNo(activeBatch.getBatchNo());

        orderItemRepository.save(item);
        int updated = batchRepository.deductStock(medicineId, activeBatch.getBatchNo(), 1);
        if (updated == 0) throw new RuntimeException("Stock was insufficient. Please try again.");
        inventoryService.checkAndCreateAlerts();
    }

    @Transactional
    public Integer placeCartOrder(Integer customerId, Map<Integer, Integer> cartItems) {
        return placeCartOrder(customerId, cartItems, "ONLINE_DELIVERY", "COD", "PENDING", null);
    }

    @Transactional
    public Integer placeCartOrder(Integer customerId, Map<Integer, Integer> cartItems, String orderType, String paymentMethod, String paymentStatus, String transactionRef) {
        com.abhapharmacy.model.Customer customer = customerRepository.findById(customerId);
        if (customer == null) throw new RuntimeException("Customer not found.");
        if (cartItems == null || cartItems.isEmpty()) throw new RuntimeException("Cart is empty.");

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) paymentMethod = "COD";
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            paymentStatus = "COD".equalsIgnoreCase(paymentMethod) ? "PENDING" : "PAID";
        }
        String orderStatus = "PAID".equalsIgnoreCase(paymentStatus) ? "CONFIRMED" : "PENDING";
        if (transactionRef == null && !"COD".equalsIgnoreCase(paymentMethod)) {
            transactionRef = "TXN_" + paymentMethod.toUpperCase() + "_" + (System.currentTimeMillis() % 10000000);
        }

        Order order = new Order();
        order.setOrderType(orderType != null && !orderType.isEmpty() ? orderType : "ONLINE_DELIVERY");
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);
        order.setPaymentMethod(paymentMethod);
        order.setTransactionRef(transactionRef);
        order.setCustomerId(customerId);
        order.setHouseNo(customer.getHouseNo());
        order.setStreet(customer.getStreet());
        order.setCity(customer.getCity());
        order.setPincode(customer.getPincode());

        Integer orderId = orderRepository.saveAndGetId(order);

        int seqNo = 1;
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            Integer medicineId = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity <= 0) continue;

            List<Batch> batches = batchRepository.findByMedicineId(medicineId);
            batches.sort((b1, b2) -> b1.getExpiryDate().compareTo(b2.getExpiryDate()));
            Batch activeBatch = null;
            for (Batch b : batches) {
                if (b.getQuantityInStock() >= quantity && b.getExpiryDate().isAfter(LocalDate.now())) {
                    activeBatch = b;
                    break;
                }
            }
            if (activeBatch == null) {
                throw new RuntimeException("Insufficient or expired stock for one of the medicines in your cart.");
            }

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setItemSeqNo(seqNo++);
            item.setQuantity(quantity);
            item.setUnitPriceAtSale(activeBatch.getSellingPrice());
            item.setMedicineId(medicineId);
            item.setBatchNo(activeBatch.getBatchNo());

            orderItemRepository.save(item);
            int updated = batchRepository.deductStock(medicineId, activeBatch.getBatchNo(), quantity);
            if (updated == 0) throw new RuntimeException("Stock was insufficient during checkout. Please try again.");
        }

        if (seqNo == 1) {
            throw new IllegalArgumentException("Cart does not contain any valid quantities.");
        }

        inventoryService.checkAndCreateAlerts();
        return orderId;
    }

    @Transactional
    public void processPosSale(Integer staffId, Integer medicineId, String batchNo, Integer quantity, BigDecimal unitPrice, BigDecimal discountAmount, String paymentMethod) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Discount amount cannot be negative.");
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Unit price cannot be negative.");

        
        if ("DEFAULT".equals(batchNo)) {
            java.util.List<Batch> batches = batchRepository.findByMedicineId(medicineId);
            if (batches == null || batches.isEmpty()) {
                throw new RuntimeException("No batches available for this medicine.");
            }
            Batch selected = null;
            for (Batch bItem : batches) {
                if (bItem.getQuantityInStock() >= quantity) {
                    selected = bItem;
                    break;
                }
            }
            if (selected == null) throw new RuntimeException("Insufficient stock in any single batch.");
            batchNo = selected.getBatchNo();
        }
        
        Batch b = batchRepository.findByMedicineIdAndBatchNo(medicineId, batchNo);

        if (b == null || b.getQuantityInStock() < quantity) {
            throw new RuntimeException("Insufficient stock in batch " + batchNo);
        }

        Order order = new Order();
        order.setOrderType("POS");
        order.setOrderStatus("COMPLETED");
        order.setPaymentStatus("PAID");
        order.setPaymentMethod(paymentMethod);
        order.setDiscountAmount(discountAmount);
        order.setStaffId(staffId);

        Integer orderId = orderRepository.saveAndGetId(order);

        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setItemSeqNo(1);
        item.setQuantity(quantity);
        item.setUnitPriceAtSale(unitPrice);
        item.setMedicineId(medicineId);
        item.setBatchNo(batchNo);

        orderItemRepository.save(item);
        int updated = batchRepository.deductStock(medicineId, batchNo, quantity);
        if (updated == 0) throw new RuntimeException("Stock was insufficient. Order rolled back.");
        inventoryService.checkAndCreateAlerts();
    }

    @Transactional
    public Integer processMultiItemPosSale(Integer staffId, Integer customerId, Map<Integer, Integer> cartItems, String paymentMethod) {
        if (cartItems == null || cartItems.isEmpty()) throw new IllegalArgumentException("Bill items cannot be empty.");

        Order order = new Order();
        order.setOrderType("POS");
        order.setOrderStatus("COMPLETED");
        order.setPaymentStatus("PAID");
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "CASH");
        order.setStaffId(staffId);
        order.setCustomerId(customerId);
        order.setDiscountAmount(BigDecimal.ZERO);

        if (customerId != null) {
            com.abhapharmacy.model.Customer c = customerRepository.findById(customerId);
            if (c != null) {
                order.setHouseNo(c.getHouseNo());
                order.setStreet(c.getStreet());
                order.setCity(c.getCity());
                order.setPincode(c.getPincode());
            }
        }

        Integer orderId = orderRepository.saveAndGetId(order);
        int seqNo = 1;

        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            Integer medicineId = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity == null || quantity <= 0) continue;

            com.abhapharmacy.model.Medicine med = inventoryService.getMedicineById(medicineId);
            BigDecimal unitPrice = (med != null && med.getLatestPrice() != null) ? med.getLatestPrice() : BigDecimal.valueOf(25.0);

            List<Batch> batches = batchRepository.findByMedicineId(medicineId);
            if (batches == null || batches.isEmpty()) {
                throw new RuntimeException("No active inventory batches for: " + (med != null ? med.getMedicineName() : ("Medicine #" + medicineId)));
            }
            batches.sort((b1, b2) -> {
                if (b1.getExpiryDate() == null) return 1;
                if (b2.getExpiryDate() == null) return -1;
                return b1.getExpiryDate().compareTo(b2.getExpiryDate());
            });

            Batch activeBatch = null;
            for (Batch b : batches) {
                if (b.getQuantityInStock() >= quantity) {
                    activeBatch = b;
                    break;
                }
            }
            if (activeBatch == null) {
                throw new RuntimeException("Insufficient stock in a single batch for: " + (med != null ? med.getMedicineName() : ("Medicine #" + medicineId)));
            }

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setItemSeqNo(seqNo++);
            item.setMedicineId(medicineId);
            item.setQuantity(quantity);
            item.setUnitPriceAtSale(unitPrice);
            item.setBatchNo(activeBatch.getBatchNo());

            orderItemRepository.save(item);
            int updated = batchRepository.deductStock(medicineId, activeBatch.getBatchNo(), quantity);
            if (updated == 0) throw new RuntimeException("Stock deduction failed for: " + (med != null ? med.getMedicineName() : ("Medicine #" + medicineId)));
        }

        if (seqNo == 1) {
            throw new IllegalArgumentException("No valid medicines selected in bill.");
        }

        inventoryService.checkAndCreateAlerts();
        return orderId;
    }

    @Transactional
    public void placePrescriptionOrder(com.abhapharmacy.model.Customer customer, Integer prescriptionId, Integer staffId, Map<Integer, Integer> cartItems) {
        if (customer == null) throw new RuntimeException("Customer not found.");
        
        Order order = new Order();
        order.setOrderType("ONLINE");
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod("COD");
        order.setCustomerId(customer.getCustomerId());
        order.setStaffId(staffId);
        order.setPrescriptionId(prescriptionId);
        order.setHouseNo(customer.getHouseNo());
        order.setStreet(customer.getStreet());
        order.setCity(customer.getCity());
        order.setPincode(customer.getPincode());

        Integer orderId = orderRepository.saveAndGetId(order);

        int seqNo = 1;
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            Integer medicineId = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity <= 0) continue;

            List<Batch> batches = batchRepository.findByMedicineId(medicineId);
            // Sort batches by expiry ASC for FEFO (First-Expiry-First-Out)
            batches.sort((b1, b2) -> b1.getExpiryDate().compareTo(b2.getExpiryDate()));

            Batch activeBatch = null;
            for (Batch b : batches) {
                if (b.getQuantityInStock() >= quantity && b.getExpiryDate().isAfter(LocalDate.now())) {
                    activeBatch = b;
                    break;
                }
            }
            if (activeBatch == null) {
                throw new RuntimeException("Insufficient or expired stock for one of the prescribed medicines.");
            }

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setItemSeqNo(seqNo++);
            item.setQuantity(quantity);
            item.setUnitPriceAtSale(activeBatch.getSellingPrice());
            item.setMedicineId(medicineId);
            item.setBatchNo(activeBatch.getBatchNo());

            orderItemRepository.save(item);
            int updated = batchRepository.deductStock(medicineId, activeBatch.getBatchNo(), quantity);
            if (updated == 0) throw new RuntimeException("Stock was insufficient during fulfillment. Please try again.");
        }

        if (seqNo == 1) {
            throw new IllegalArgumentException("Prescription fulfillment does not contain any valid quantities.");
        }

        inventoryService.checkAndCreateAlerts();
    }

    public java.util.List<Order> getOrderHistory(Integer customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public java.util.List<OrderItem> getItemsForOrder(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public java.util.List<Order> getActiveOrders() {
        return orderRepository.findActiveOrders();
    }

    public void completeOrder(Integer orderId) {
        orderRepository.updateOrderStatus(orderId, "COMPLETED", "PAID");
    }

    @Transactional
    public void refillOrder(Integer customerId, Integer orderId) {
        java.util.List<OrderItem> items = getItemsForOrder(orderId);
        if (items == null || items.isEmpty()) throw new RuntimeException("No items found in selected order to refill.");
        java.util.Map<Integer, Integer> cartItems = new java.util.HashMap<>();
        for (OrderItem it : items) {
            cartItems.put(it.getMedicineId(), it.getQuantity());
        }
        placeCartOrder(customerId, cartItems);
    }

    public List<Map<String, Object>> getScheduleHDispensations() {
        return orderRepository.findScheduleHDispensations();
    }

    public Map<String, Object> getRevenueAnalytics() {
        return orderRepository.getRevenueAnalytics();
    }
}
