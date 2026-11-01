package com.abhapharmacy.controller;

import com.abhapharmacy.model.Alert;
import com.abhapharmacy.model.Batch;
import com.abhapharmacy.model.Medicine;
import com.abhapharmacy.model.Prescription;
import com.abhapharmacy.service.InventoryService;
import com.abhapharmacy.service.OrderService;
import com.abhapharmacy.service.PrescriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final PrescriptionService prescriptionService;
    private final com.abhapharmacy.service.StaffService staffService;
    private final com.abhapharmacy.service.CustomerService customerService;
    private final com.abhapharmacy.service.AiService aiService;

    public AdminController(InventoryService inventoryService, OrderService orderService, PrescriptionService prescriptionService, com.abhapharmacy.service.StaffService staffService, com.abhapharmacy.service.CustomerService customerService, com.abhapharmacy.service.AiService aiService) {
        this.aiService = aiService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.prescriptionService = prescriptionService;
        this.staffService = staffService;
        this.customerService = customerService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String keyword, HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        
        inventoryService.checkAndCreateAlerts();
        List<Alert> alerts = inventoryService.getActiveAlerts();
        List<Prescription> prescriptions = prescriptionService.getPendingPrescriptions();
        List<Medicine> inventory = inventoryService.getAllMedicines();
        List<com.abhapharmacy.model.Order> activeOrders = orderService.getActiveOrders();

        java.util.Map<Integer, java.util.List<com.abhapharmacy.model.OrderItem>> orderItemsMap = new java.util.HashMap<>();
        for (com.abhapharmacy.model.Order o : activeOrders) {
            orderItemsMap.put(o.getOrderId(), orderService.getItemsForOrder(o.getOrderId()));
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            List<Medicine> searchResults = inventoryService.searchAllMedicinesWithStock(keyword);
            model.addAttribute("inventorySearch", searchResults);
            
            java.util.Map<Integer, List<Batch>> batchesMap = new java.util.HashMap<>();
            for (Medicine m : searchResults) {
                batchesMap.put(m.getMedicineId(), inventoryService.getBatchesForMedicine(m.getMedicineId()));
            }
            model.addAttribute("batchesMap", batchesMap);
        }

        model.addAttribute("alerts", alerts);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("inventory", inventory);
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("orderItemsMap", orderItemsMap);
        model.addAttribute("suppliers", inventoryService.getAllSuppliers());
        
        if ("OWNER".equals(session.getAttribute("staffRole"))) {
            model.addAttribute("analytics", orderService.getRevenueAnalytics());
            model.addAttribute("staffCount", staffService.getAllStaff().size());
            model.addAttribute("scheduleHCount", orderService.getScheduleHDispensations().size());
        }

        return "admin/dashboard";
    }

    @GetMapping("/pos")
    public String pos(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if ("OWNER".equals(session.getAttribute("staffRole"))) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("medicines", inventoryService.getAllMedicines());
        model.addAttribute("customers", customerService.getAllCustomers());
        String staffName = (String) session.getAttribute("staffName");
        model.addAttribute("staffName", staffName != null ? staffName : "Pharmacist");
        return "admin/pos";
    }

    @PostMapping("/medicine/add")
    public String addMedicine(Medicine medicine, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if ("OWNER".equals(session.getAttribute("staffRole"))) {
            redirectAttributes.addFlashAttribute("error", "Adding medicines is an operational task restricted to pharmacy dispensing staff.");
            return "redirect:/admin/dashboard";
        }
        try {
            inventoryService.saveMedicine(medicine);
            redirectAttributes.addFlashAttribute("success", "Medicine added successfully!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Error saving medicine: Check fields or duplicate data.");
        }
        return "redirect:/admin/grn";
    }

    @PostMapping("/batch/add")
    public String addBatch(Batch batch, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if ("OWNER".equals(session.getAttribute("staffRole"))) {
            redirectAttributes.addFlashAttribute("error", "Inwarding stock batches is restricted to pharmacy dispensing staff.");
            return "redirect:/admin/dashboard";
        }
        try {
            inventoryService.saveBatch(batch);
            redirectAttributes.addFlashAttribute("success", "Batch added successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("supplierid") || msg.contains("supplier")) {
                redirectAttributes.addFlashAttribute("error", "Error: The selected Supplier does not exist in the system.");
            } else if (msg.contains("medicineid") || msg.contains("medicine")) {
                redirectAttributes.addFlashAttribute("error", "Error: The selected Medicine does not exist in the system.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error saving batch: Inconsistent or duplicate data provided.");
            }
        }
        return "redirect:/admin/grn";
    }

    @PostMapping("/pos/checkout")
    public String checkout(@RequestParam(required = false) Integer customerId,
                           @RequestParam(required = false) String newCustomerFirstName,
                           @RequestParam(required = false) String newCustomerLastName,
                           @RequestParam(required = false) String newCustomerPhone,
                           @RequestParam(required = false) String newCustomerCity,
                           @RequestParam(required = false) Integer medicineId, 
                           @RequestParam(required = false) String batchNo, 
                           @RequestParam(required = false) Integer quantity, 
                           @RequestParam(required = false) BigDecimal unitPrice, 
                           @RequestParam(required = false) BigDecimal discountAmount, 
                           @RequestParam(defaultValue = "CASH") String paymentMethod, 
                           @RequestParam(required = false) String customerName,
                           @RequestParam(required = false) String customerPhone,
                           @RequestParam java.util.Map<String, String> allParams,
                           HttpSession session, 
                           org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        Integer staffId = (Integer) session.getAttribute("staffId");
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (batchNo == null || batchNo.trim().isEmpty()) batchNo = "DEFAULT";

        try {
            // 1. Resolve or Register Customer
            String clientName = "Walk-in Patient";
            if (customerId != null) {
                com.abhapharmacy.model.Customer c = customerService.getCustomerById(customerId);
                if (c != null) {
                    clientName = c.getFirstName() + " " + (c.getLastName() != null ? c.getLastName() : "");
                }
            } else if (newCustomerFirstName != null && !newCustomerFirstName.trim().isEmpty()) {
                com.abhapharmacy.model.Customer newCust = customerService.registerWalkInCustomer(
                    newCustomerFirstName, newCustomerLastName, newCustomerPhone, newCustomerCity
                );
                customerId = newCust.getCustomerId();
                clientName = newCust.getFirstName() + " " + (newCust.getLastName() != null ? newCust.getLastName() : "");
            } else if (customerName != null && !customerName.trim().isEmpty()) {
                clientName = customerName.trim();
            }

            // 2. Multi-Medicine Cart check
            java.util.Map<Integer, Integer> cartItems = new java.util.HashMap<>();
            for (java.util.Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("qty_")) {
                    try {
                        int medId = Integer.parseInt(entry.getKey().substring(4));
                        int qty = Integer.parseInt(entry.getValue());
                        if (qty > 0) {
                            cartItems.put(medId, qty);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            if (!cartItems.isEmpty()) {
                Integer orderId = orderService.processMultiItemPosSale(staffId, customerId, cartItems, paymentMethod);
                redirectAttributes.addFlashAttribute("success", 
                    "Dispense order #ORD-" + orderId + " completed successfully for " + clientName.trim() + "! (" + cartItems.size() + " formulations dispensed)");
            } else if (medicineId != null && quantity != null && quantity > 0) {
                if (unitPrice == null) {
                    com.abhapharmacy.model.Medicine med = inventoryService.getMedicineById(medicineId);
                    unitPrice = (med != null && med.getLatestPrice() != null) ? med.getLatestPrice() : BigDecimal.valueOf(50);
                }
                orderService.processPosSale(staffId, medicineId, batchNo, quantity, unitPrice, discountAmount, paymentMethod);
                redirectAttributes.addFlashAttribute("success", 
                    "Sale of " + quantity + " unit(s) completed successfully for " + clientName.trim() + "!");
            } else {
                throw new IllegalArgumentException("No medicines were added to the bill.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/pos";
        }
        return "redirect:/admin/pos";
    }

    @PostMapping("/prescriptions/verify")
    public String verifyPrescription(@RequestParam Integer prescriptionId, @RequestParam String status, @RequestParam(required = false) String remarks, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        Integer staffId = (Integer) session.getAttribute("staffId");
        if (remarks == null || remarks.trim().isEmpty()) {
            remarks = "REJECTED".equalsIgnoreCase(status) ? "Rejected: Incomplete or illegible medical prescription details under Schedule H protocol." : "Prescription approved by registered pharmacist.";
        }
        prescriptionService.verifyPrescription(prescriptionId, staffId, status, remarks, null, orderService);
        redirectAttrs.addFlashAttribute("success", "Prescription status updated to " + status);
        return "redirect:/admin/prescriptions";
    }

    @GetMapping("/prescriptions/fulfill")
    public String fulfillPrescriptionForm(@RequestParam Integer prescriptionId, HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        
        com.abhapharmacy.model.Prescription p = prescriptionService.getPendingPrescriptions().stream()
                .filter(pr -> pr.getPrescriptionId().equals(prescriptionId))
                .findFirst().orElse(null);
                
        if (p == null) return "redirect:/admin/dashboard";
        
        java.util.List<com.abhapharmacy.model.Medicine> medicines = inventoryService.getAllMedicines();
        model.addAttribute("prescription", p);
        model.addAttribute("medicines", medicines);
        model.addAttribute("aiAnalysis", aiService.analyzePrescription(p, medicines));
        return "admin/prescription_fulfill";
    }

    @PostMapping("/prescriptions/fulfill")
    public String fulfillPrescription(@RequestParam Integer prescriptionId, @RequestParam String remarks, @RequestParam java.util.Map<String, String> formParams, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        Integer staffId = (Integer) session.getAttribute("staffId");

        java.util.Map<Integer, Integer> cartItems = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, String> entry : formParams.entrySet()) {
            if (entry.getKey().startsWith("qty_")) {
                try {
                    int medicineId = Integer.parseInt(entry.getKey().substring(4));
                    int qty = Integer.parseInt(entry.getValue());
                    if (qty > 0) {
                        cartItems.put(medicineId, qty);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        try {
            prescriptionService.verifyPrescription(prescriptionId, staffId, "APPROVED", remarks, cartItems, orderService);
            redirectAttrs.addFlashAttribute("success", "Prescription approved and order generated!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/alerts/resolve")
    public String resolveAlert(@RequestParam Integer alertId,
                               @RequestParam(required = false, defaultValue = "/admin/alerts") String redirect,
                               HttpSession session,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        Integer staffId = (Integer) session.getAttribute("staffId");
        inventoryService.resolveAlert(alertId, staffId);
        redirectAttrs.addFlashAttribute("success", "Alert #" + alertId + " marked as resolved.");
        return "redirect:" + (redirect.startsWith("/admin") ? redirect : "/admin/alerts");
    }

    @GetMapping("/staff")
    public String staffManagement(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if (!"OWNER".equals(session.getAttribute("staffRole"))) return "redirect:/admin/dashboard";
        java.util.List<com.abhapharmacy.model.Staff> staffList = staffService.getAllStaff();
        model.addAttribute("staffList", staffList);

        long pharmacistCount = staffList.stream().filter(s -> "PHARMACIST".equalsIgnoreCase(s.getRole())).count();
        java.math.BigDecimal totalPayroll = staffList.stream()
                .filter(s -> !"INACTIVE".equalsIgnoreCase(s.getRole()) && s.getSalary() != null)
                .map(com.abhapharmacy.model.Staff::getSalary)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("pharmacistCount", pharmacistCount);
        model.addAttribute("totalPayroll", totalPayroll);
        return "admin/staff-management";
    }

    @PostMapping("/staff/add")
    public String addStaff(com.abhapharmacy.model.Staff staff, @RequestParam String rawPassword, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if (!"OWNER".equals(session.getAttribute("staffRole"))) return "redirect:/admin/dashboard";
        try {
            staffService.addStaff(staff, rawPassword);
            redirectAttrs.addFlashAttribute("success", "Staff member added successfully.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("error", "Email already exists.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "An error occurred.");
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/deactivate")
    public String deactivateStaff(@RequestParam Integer targetStaffId, HttpSession session) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if (!"OWNER".equals(session.getAttribute("staffRole"))) return "redirect:/admin/dashboard";
        staffService.deactivateStaff(targetStaffId);
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/update")
    public String updateStaff(@RequestParam Integer targetStaffId,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String role,
                              @RequestParam(required = false) String licenseNumber,
                              @RequestParam(required = false) BigDecimal salary,
                              @RequestParam(required = false) String newPassword,
                              HttpSession session,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if (!"OWNER".equals(session.getAttribute("staffRole"))) return "redirect:/admin/dashboard";
        try {
            staffService.updateStaffDetails(targetStaffId, firstName, lastName, role, licenseNumber, salary, newPassword);
            redirectAttrs.addFlashAttribute("success", "Staff details updated successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to update staff: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/reactivate")
    public String reactivateStaff(@RequestParam Integer targetStaffId, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if (!"OWNER".equals(session.getAttribute("staffRole"))) return "redirect:/admin/dashboard";
        staffService.reactivateStaff(targetStaffId);
        redirectAttrs.addFlashAttribute("success", "Staff account reactivated as Pharmacist.");
        return "redirect:/admin/staff";
    }

    @PostMapping("/orders/complete")
    public String completeOrder(@RequestParam Integer orderId, HttpSession session) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        orderService.completeOrder(orderId);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/supplier/add")
    public String addSupplier(com.abhapharmacy.model.Supplier supplier, @RequestParam(required = false) String phone, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        try {
            inventoryService.saveSupplier(supplier, phone);
            redirectAttributes.addFlashAttribute("success", "Supplier added successfully!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Error saving supplier: Check fields or duplicate data.");
        }
        return "redirect:/admin/suppliers";
    }

    @GetMapping("/suppliers")
    public String suppliers(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        model.addAttribute("suppliers", inventoryService.getAllSuppliersWithDetails());
        return "admin/suppliers";
    }

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        if (!"OWNER".equals(session.getAttribute("staffRole"))) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("analytics", orderService.getRevenueAnalytics());
        return "admin/reports";
    }

    @PostMapping("/customers/register")
    public String registerCustomer(com.abhapharmacy.model.Customer customer, @RequestParam String rawPassword, @RequestParam(required = false) String phone, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        try {
            com.abhapharmacy.service.CustomerService customerService = org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(session.getServletContext()).getBean(com.abhapharmacy.service.CustomerService.class);
            customerService.registerCustomer(customer, rawPassword, phone);
            redirectAttrs.addFlashAttribute("success", "Customer registered successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("error", "Email already exists.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "An error occurred during registration.");
        }
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/prescriptions")
    public String prescriptions(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        model.addAttribute("prescriptions", prescriptionService.getPendingPrescriptions());
        model.addAttribute("processedPrescriptions", prescriptionService.getRecentProcessedPrescriptions());
        return "admin/prescriptions";
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        List<com.abhapharmacy.model.Order> activeOrders = orderService.getActiveOrders();
        java.util.Map<Integer, java.util.List<com.abhapharmacy.model.OrderItem>> orderItemsMap = new java.util.HashMap<>();
        for (com.abhapharmacy.model.Order o : activeOrders) {
            orderItemsMap.put(o.getOrderId(), orderService.getItemsForOrder(o.getOrderId()));
        }
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("orderItemsMap", orderItemsMap);
        return "admin/orders";
    }

    @GetMapping({"/schedule_h", "/schedule-h"})
    public String scheduleH(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        model.addAttribute("dispensations", orderService.getScheduleHDispensations());
        return "admin/schedule_h";
    }

    @GetMapping("/grn")
    public String grn(@RequestParam(required = false) Integer supplierId, HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        model.addAttribute("medicines", inventoryService.getAllMedicines());
        List<com.abhapharmacy.model.Supplier> suppliers = inventoryService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        if (supplierId != null) {
            suppliers.stream()
                .filter(s -> s.getSupplierId() != null && s.getSupplierId().equals(supplierId))
                .findFirst()
                .ifPresent(s -> model.addAttribute("selectedSupplier", s));
        }
        return "admin/grn";
    }

    @GetMapping("/alerts")
    public String alerts(HttpSession session, Model model) {
        if (session.getAttribute("staffId") == null) return "redirect:/login/admin";
        inventoryService.checkAndCreateAlerts();
        List<Alert> activeAlerts = inventoryService.getActiveAlerts();
        List<Alert> resolvedAlerts = inventoryService.getResolvedAlerts();

        long lowStockCount = activeAlerts.stream().filter(a -> "LOW_STOCK".equalsIgnoreCase(a.getAlertType())).count();
        long expiringCount = activeAlerts.stream().filter(a -> "EXPIRING_SOON".equalsIgnoreCase(a.getAlertType()) || "EXPIRED".equalsIgnoreCase(a.getAlertType())).count();

        model.addAttribute("alerts", activeAlerts);
        model.addAttribute("resolvedAlerts", resolvedAlerts);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("expiringCount", expiringCount);
        model.addAttribute("resolvedCount", resolvedAlerts.size());
        return "admin/alerts";
    }
}
