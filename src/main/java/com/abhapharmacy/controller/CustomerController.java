package com.abhapharmacy.controller;

import com.abhapharmacy.model.Medicine;
import com.abhapharmacy.model.Order;
import com.abhapharmacy.model.OrderItem;
import com.abhapharmacy.service.InventoryService;
import com.abhapharmacy.service.PrescriptionService;
import com.abhapharmacy.service.CustomerService;
import com.abhapharmacy.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final InventoryService inventoryService;
    private final PrescriptionService prescriptionService;
    private final OrderService orderService;
    private final com.abhapharmacy.service.AiService aiService;

    public CustomerController(CustomerService customerService, InventoryService inventoryService, PrescriptionService prescriptionService, OrderService orderService, com.abhapharmacy.service.AiService aiService) {
        this.aiService = aiService;
        this.customerService = customerService;
        this.inventoryService = inventoryService;
        this.prescriptionService = prescriptionService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");

        List<Order> orders = orderService.getOrderHistory(customerId);
        Map<Integer, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Order o : orders) {
            orderItemsMap.put(o.getOrderId(), orderService.getItemsForOrder(o.getOrderId()));
        }

        model.addAttribute("medicines", inventoryService.getAllOTCMedicines());
        model.addAttribute("prescriptions", prescriptionService.getPrescriptionHistory(customerId));
        model.addAttribute("orders", orders);
        model.addAttribute("orderItemsMap", orderItemsMap);
        return "customer/dashboard";
    }

    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) String keyword, HttpSession session, Model model) {
        List<Medicine> medicines;
        if (keyword != null && !keyword.isEmpty()) {
            medicines = inventoryService.searchOTCMedicines(keyword);
        } else {
            medicines = inventoryService.getAllOTCMedicines();
        }
        model.addAttribute("medicines", medicines);
        java.util.Set<String> categories = medicines.stream()
                .map(Medicine::getCategory)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
        model.addAttribute("categories", categories);
        return "customer/catalog";
    }

    @GetMapping({"/medicine/{id}", "/medicines/{id}"})
    public String medicineDetails(@PathVariable Integer id, HttpSession session, Model model) {
        Medicine medicine = inventoryService.getMedicineById(id);
        if (medicine == null) {
            return "redirect:/customer/catalog";
        }
        model.addAttribute("medicine", medicine);
        return "customer/medicine_details";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");
        com.abhapharmacy.model.Customer customer = customerService.getCustomerById(customerId);
        List<String> phones = customerService.getCustomerPhones(customerId);
        String phone = (phones != null && !phones.isEmpty()) ? phones.get(0) : "";
        model.addAttribute("customer", customer);
        model.addAttribute("phone", phone);
        model.addAttribute("medicines", inventoryService.getAllOTCMedicines());
        return "customer/cart_checkout";
    }

    @GetMapping("/prescription/upload")
    public String uploadPrescription(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");
        model.addAttribute("prescriptions", prescriptionService.getPrescriptionHistory(customerId));
        return "customer/prescription_upload";
    }

    @GetMapping("/orders")
    public String orderTracking(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");
        
        List<Order> orders = orderService.getOrderHistory(customerId);
        Map<Integer, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Order o : orders) {
            orderItemsMap.put(o.getOrderId(), orderService.getItemsForOrder(o.getOrderId()));
        }
        model.addAttribute("orders", orders);
        model.addAttribute("orderItemsMap", orderItemsMap);
        
        return "customer/order_tracking";
    }

    @GetMapping("/refill")
    public String refill(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");
        model.addAttribute("prescriptions", prescriptionService.getPrescriptionHistory(customerId));
        List<Order> orders = orderService.getOrderHistory(customerId);
        Map<Integer, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Order o : orders) {
            orderItemsMap.put(o.getOrderId(), orderService.getItemsForOrder(o.getOrderId()));
        }
        model.addAttribute("orders", orders);
        model.addAttribute("orderItemsMap", orderItemsMap);
        return "customer/refill";
    }

    @PostMapping("/refill/order")
    public String refillOrderPost(@RequestParam Integer orderId, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");
        try {
            orderService.refillOrder(customerId, orderId);
            redirectAttrs.addFlashAttribute("success", "Refill order submitted successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Refill failed: " + e.getMessage());
        }
        return "redirect:/customer/orders";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");
        com.abhapharmacy.model.Customer customer = customerService.getCustomerById(customerId);
        List<String> phones = customerService.getCustomerPhones(customerId);
        String phone = (phones != null && !phones.isEmpty()) ? phones.get(0) : "";
        model.addAttribute("customer", customer);
        model.addAttribute("phone", phone);
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(com.abhapharmacy.model.Customer customer, @RequestParam(required = false) String phone, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");
        customer.setCustomerId(customerId);
        try {
            customerService.updateCustomerProfile(customer, phone);
            session.setAttribute("customerName", customer.getFirstName());
            redirectAttrs.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/customer/profile";
    }

    @PostMapping("/prescription/upload")
    public String uploadPrescriptionPost(@RequestParam("file") org.springframework.web.multipart.MultipartFile file, @RequestParam String instructions, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");

        if (file.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/customer/prescription/upload";
        }

        String origName = file.getOriginalFilename();
        if (origName == null || (!origName.toLowerCase().endsWith(".jpg")
                && !origName.toLowerCase().endsWith(".jpeg")
                && !origName.toLowerCase().endsWith(".png")
                && !origName.toLowerCase().endsWith(".pdf"))) {
            redirectAttrs.addFlashAttribute("error", "Unsupported file type. Only JPG, PNG, and PDF formats are permitted under Schedule H protocol.");
            return "redirect:/customer/prescription/upload";
        }

        try {
            java.io.File uploadDir = new java.io.File("app_data/prescriptions");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String filename = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            java.nio.file.Path filePath = java.nio.file.Paths.get("app_data/prescriptions", filename);
            file.transferTo(filePath);

            prescriptionService.uploadPrescription(customerId, filename, instructions);
            redirectAttrs.addFlashAttribute("success", "Prescription uploaded successfully and queued for pharmacist review.");
        } catch (java.io.IOException e) {
            redirectAttrs.addFlashAttribute("error", "Failed to upload file.");
        }

        return "redirect:/customer/orders";
    }

    @PostMapping("/order/otc")
    public String orderOtc(@RequestParam Integer medicineId, @RequestParam(required = false, defaultValue = "1") Integer quantity, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");

        if (quantity != null && (quantity < 1 || quantity > 10)) {
            redirectAttrs.addFlashAttribute("error", "Quantity validation failed: Retail OTC formulation orders cannot exceed 10 units per order.");
            return "redirect:/customer/medicine/" + medicineId;
        }

        try {
            if (quantity != null && quantity > 1) {
                Map<Integer, Integer> cartItems = new HashMap<>();
                cartItems.put(medicineId, quantity);
                orderService.placeCartOrder(customerId, cartItems);
            } else {
                orderService.placeOtcOrder(customerId, medicineId);
            }
            redirectAttrs.addFlashAttribute("success", "Order placed successfully!");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/medicine/" + medicineId;
        }

        return "redirect:/customer/orders";
    }

    @PostMapping("/order/cart")
    public String orderCart(@RequestParam Map<String, String> formParams, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (session.getAttribute("customerId") == null) return "redirect:/login/customer";
        Integer customerId = (Integer) session.getAttribute("customerId");

        Map<Integer, Integer> cartItems = new HashMap<>();
        for (Map.Entry<String, String> entry : formParams.entrySet()) {
            if (entry.getKey().startsWith("qty_")) {
                try {
                    int medicineId = Integer.parseInt(entry.getKey().substring(4));
                    int qty = Integer.parseInt(entry.getValue());
                    if (qty > 0) {
                        cartItems.put(medicineId, qty);
                    }
                } catch (NumberFormatException e) {
                    redirectAttrs.addFlashAttribute("error", "Invalid quantity format provided.");
                    return "redirect:/customer/cart";
                }
            }
        }

        if (cartItems.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/customer/cart";
        }

        String orderType = formParams.getOrDefault("orderType", "ONLINE_DELIVERY");
        String paymentMethod = formParams.getOrDefault("paymentMethod", "COD");
        String paymentStatus = formParams.getOrDefault("paymentStatus", "COD".equalsIgnoreCase(paymentMethod) ? "PENDING" : "PAID");
        String transactionRef = formParams.get("transactionRef");

        try {
            Integer orderId = orderService.placeCartOrder(customerId, cartItems, orderType, paymentMethod, paymentStatus, transactionRef);
            if ("PAID".equalsIgnoreCase(paymentStatus)) {
                redirectAttrs.addFlashAttribute("success", "Payment Verified! Order #ORD-" + orderId + " settled via " + paymentMethod + " (Ref: " + (transactionRef != null ? transactionRef : "INSTANT-SETTLED") + ")");
            } else {
                redirectAttrs.addFlashAttribute("success", "Pharmacy order #ORD-" + orderId + " placed successfully!");
            }
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/customer/orders";
    }

    @org.springframework.web.bind.annotation.ResponseBody
    @PostMapping("/ai/ask")
    public java.util.Map<String, String> askAi(jakarta.servlet.http.HttpServletRequest request) {
        String q = request.getParameter("query");
        if (q == null || q.trim().isEmpty()) {
            try {
                String body = new String(request.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                if (body.contains("\"query\"")) {
                    int idx = body.indexOf("\"query\"");
                    int colon = body.indexOf(":", idx);
                    int start = body.indexOf("\"", colon) + 1;
                    int end = body.indexOf("\"", start);
                    if (start > 0 && end > start) {
                        q = body.substring(start, end);
                    }
                }
            } catch (Exception ignored) {}
        }
        String reply = aiService.askAiPharmacist(q, inventoryService.getAllMedicines());
        java.util.Map<String, String> res = new java.util.HashMap<>();
        res.put("reply", reply);
        return res;
    }
}
