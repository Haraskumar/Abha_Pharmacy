package com.abhapharmacy.service;

import com.abhapharmacy.model.Staff;
import com.abhapharmacy.repository.StaffRepository;
import com.abhapharmacy.util.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public Staff authenticate(String email, String rawPassword) {
        Staff s = staffRepository.findByEmail(email);
        if (s != null && SecurityUtil.verifyPassword(rawPassword, s.getPasswordHash())) {
            return s;
        }
        return null;
    }

    public java.util.List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public void addStaff(Staff s, String rawPassword) {
        s.setPasswordHash(SecurityUtil.hashPassword(rawPassword));
        if (s.getRole() == null || s.getRole().isEmpty()) {
            s.setRole("PHARMACIST");
        }
        staffRepository.save(s);
    }

    public void deactivateStaff(Integer staffId) {
        staffRepository.updateRole(staffId, "INACTIVE");
    }

    public void updateRole(Integer staffId, String role) {
        staffRepository.updateRole(staffId, role);
    }

    public void updateStaffDetails(Integer staffId, String firstName, String lastName, String role, String licenseNumber, java.math.BigDecimal salary, String rawPassword) {
        String passwordHash = null;
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            passwordHash = SecurityUtil.hashPassword(rawPassword.trim());
        }
        staffRepository.updateStaffDetails(staffId, firstName, lastName, role, licenseNumber, salary, passwordHash);
    }

    public void reactivateStaff(Integer staffId) {
        staffRepository.updateRole(staffId, "PHARMACIST");
    }
}
