package com.abhapharmacy.service;

import com.abhapharmacy.model.Customer;
import com.abhapharmacy.repository.CustomerRepository;
import com.abhapharmacy.util.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer authenticate(String email, String rawPassword) {
        Customer c = customerRepository.findByEmail(email);
        if (c != null && SecurityUtil.verifyPassword(rawPassword, c.getPasswordHash())) {
            return c;
        }
        return null;
    }

    @org.springframework.transaction.annotation.Transactional
    public void registerCustomer(Customer c, String rawPassword, String phone) {
        if (c.getFirstName() != null && c.getFirstName().length() > 50) throw new IllegalArgumentException("First name cannot exceed 50 characters");
        if (c.getLastName() != null && c.getLastName().length() > 50) throw new IllegalArgumentException("Last name cannot exceed 50 characters");
        if (c.getHouseNo() != null && c.getHouseNo().length() > 50) throw new IllegalArgumentException("House number cannot exceed 50 characters");
        if (c.getStreet() != null && c.getStreet().length() > 100) throw new IllegalArgumentException("Street cannot exceed 100 characters");
        if (c.getCity() != null && c.getCity().length() > 50) throw new IllegalArgumentException("City cannot exceed 50 characters");
        if (c.getPincode() != null && c.getPincode().length() > 10) throw new IllegalArgumentException("Pincode cannot exceed 10 characters");
        if (phone != null && phone.length() > 15) throw new IllegalArgumentException("Phone number cannot exceed 15 characters");
        
        c.setPasswordHash(SecurityUtil.hashPassword(rawPassword));
        Integer id = customerRepository.saveAndGetId(c);
        
        if (phone != null && !phone.trim().isEmpty()) {
            customerRepository.savePhone(id, phone.trim());
        }
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    public java.util.List<String> getCustomerPhones(Integer id) {
        return customerRepository.findPhonesByCustomerId(id);
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateCustomerProfile(Customer c, String phone) {
        if (c.getFirstName() != null && c.getFirstName().length() > 50) throw new IllegalArgumentException("First name cannot exceed 50 characters");
        if (c.getLastName() != null && c.getLastName().length() > 50) throw new IllegalArgumentException("Last name cannot exceed 50 characters");
        if (c.getHouseNo() != null && c.getHouseNo().length() > 50) throw new IllegalArgumentException("House number cannot exceed 50 characters");
        if (c.getStreet() != null && c.getStreet().length() > 100) throw new IllegalArgumentException("Street cannot exceed 100 characters");
        if (c.getCity() != null && c.getCity().length() > 50) throw new IllegalArgumentException("City cannot exceed 50 characters");
        if (c.getPincode() != null && c.getPincode().length() > 10) throw new IllegalArgumentException("Pincode cannot exceed 10 characters");
        if (phone != null && phone.length() > 15) throw new IllegalArgumentException("Phone number cannot exceed 15 characters");

        customerRepository.updateProfile(c);
        if (phone != null && !phone.trim().isEmpty()) {
            customerRepository.updatePhone(c.getCustomerId(), phone);
        }
    }

    public java.util.List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    @org.springframework.transaction.annotation.Transactional
    public Customer registerWalkInCustomer(String firstName, String lastName, String phone, String city) {
        Customer c = new Customer();
        c.setFirstName(firstName != null && !firstName.trim().isEmpty() ? firstName.trim() : "Walk-in");
        c.setLastName(lastName != null && !lastName.trim().isEmpty() ? lastName.trim() : "Patient");
        c.setCity(city != null && !city.trim().isEmpty() ? city.trim() : "Koramangala");
        c.setHouseNo("Counter");
        c.setStreet("Local");
        c.setPincode("560034");
        String sanitizedPhone = phone != null ? phone.replaceAll("[^0-9]", "") : "";
        if (sanitizedPhone.isEmpty()) sanitizedPhone = String.valueOf(System.currentTimeMillis() % 10000000000L);
        c.setEmail("walkin_" + sanitizedPhone + "@abhapharmacy.com");
        c.setPasswordHash(SecurityUtil.hashPassword("walkin123"));
        c.setRegistrationDate(java.time.LocalDate.now());
        
        Integer id = customerRepository.saveAndGetId(c);
        c.setCustomerId(id);
        if (phone != null && !phone.trim().isEmpty()) {
            customerRepository.savePhone(id, phone.trim());
        }
        return c;
    }
}
