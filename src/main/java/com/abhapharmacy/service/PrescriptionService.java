package com.abhapharmacy.service;

import com.abhapharmacy.model.Prescription;
import com.abhapharmacy.model.Order;
import com.abhapharmacy.repository.PrescriptionRepository;
import com.abhapharmacy.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final OrderRepository orderRepository;
    private final JdbcTemplate jdbcTemplate;
    private final com.abhapharmacy.repository.CustomerRepository customerRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, OrderRepository orderRepository, JdbcTemplate jdbcTemplate, com.abhapharmacy.repository.CustomerRepository customerRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.orderRepository = orderRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void uploadPrescription(Integer customerId, String imageUrl, String instructions) {
        Prescription p = new Prescription();
        p.setCustomerId(customerId);
        p.setPrescriptionImageUrl(imageUrl);
        p.setInstructions(instructions);
        prescriptionRepository.save(p);
    }

    public List<Prescription> getPendingPrescriptions() {
        List<Prescription> list = prescriptionRepository.findPending();
        for (Prescription p : list) {
            p.setCustomer(customerRepository.findById(p.getCustomerId()));
        }
        return list;
    }

    public List<Prescription> getPrescriptionHistory(Integer customerId) {
        return prescriptionRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void verifyPrescription(Integer prescriptionId, Integer staffId, String status, String remarks, java.util.Map<Integer, Integer> cartItems, com.abhapharmacy.service.OrderService orderService) {
        jdbcTemplate.update("UPDATE PRESCRIPTION SET VerificationStatus = ?, StaffID = ?, VerificationDate = CURRENT_DATE, VerificationTime = CURRENT_TIME, PharmacistRemarks = ? WHERE PrescriptionID = ?",
                status, staffId, remarks, prescriptionId);
        
        if ("APPROVED".equals(status) && cartItems != null && !cartItems.isEmpty()) {
            Prescription p = prescriptionRepository.findById(prescriptionId);
            com.abhapharmacy.model.Customer customer = customerRepository.findById(p.getCustomerId());
            
            // Re-use logic from cart but linked to prescription and staff
            orderService.placePrescriptionOrder(customer, prescriptionId, staffId, cartItems);
        }
    }

    public List<Prescription> getRecentProcessedPrescriptions() {
        List<Prescription> list = prescriptionRepository.findRecentProcessed();
        for (Prescription p : list) {
            p.setCustomer(customerRepository.findById(p.getCustomerId()));
        }
        return list;
    }
}
