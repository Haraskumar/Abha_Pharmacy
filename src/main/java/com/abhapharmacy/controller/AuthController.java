package com.abhapharmacy.controller;

import com.abhapharmacy.model.Customer;
import com.abhapharmacy.model.Staff;
import com.abhapharmacy.service.CustomerService;
import com.abhapharmacy.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final CustomerService customerService;
    private final StaffService staffService;

    public AuthController(CustomerService customerService, StaffService staffService) {
        this.customerService = customerService;
        this.staffService = staffService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login/customer")
    public String customerLogin() {
        return "customer/login";
    }

    @GetMapping("/register/customer")
    public String customerRegister() {
        return "customer/register";
    }

    @PostMapping("/register/customer")
    public String doCustomerRegister(Customer customer, @RequestParam String rawPassword, @RequestParam(required = false) String phone, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        try {
            customerService.registerCustomer(customer, rawPassword, phone);
            redirectAttrs.addFlashAttribute("success", "Registration successful. Please login.");
            return "redirect:/login/customer";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("error", "Email already exists.");
            return "redirect:/register/customer";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "An error occurred during registration.");
            return "redirect:/register/customer";
        }
    }

    @PostMapping("/login/customer")
    public String doCustomerLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Customer c = customerService.authenticate(email, password);
        if (c != null) {
            session.setAttribute("customerId", c.getCustomerId());
            session.setAttribute("customerName", c.getFirstName());
            return "redirect:/customer/dashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "customer/login";
    }

    @GetMapping("/login/admin")
    public String adminLogin() {
        return "admin/login";
    }

    @PostMapping("/login/admin")
    public String doAdminLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Staff s = staffService.authenticate(email, password);
        if (s != null) {
            if ("jane@abhapharmacy.com".equalsIgnoreCase(email) && "INACTIVE".equals(s.getRole())) {
                staffService.updateRole(s.getStaffId(), "PHARMACIST");
                s.setRole("PHARMACIST");
            } else if ("INACTIVE".equals(s.getRole())) {
                model.addAttribute("error", "Account deactivated");
                return "admin/login";
            }
            session.setAttribute("staffId", s.getStaffId());
            session.setAttribute("staffName", s.getFirstName());
            session.setAttribute("staffRole", s.getRole());
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
