package com.abhapharmacy.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Clean Spring HandlerInterceptor enforcing authentication and role-based access control (RBAC).
 * Eliminates boilerplate session verification across individual controller methods.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // 1. Staff / Admin Routes
        if (uri.startsWith("/admin")) {
            if (session == null || session.getAttribute("staffId") == null) {
                response.sendRedirect(request.getContextPath() + "/login/admin");
                return false;
            }

            // Role enforcement: Reports and Staff management restricted to OWNER only
            String role = (String) session.getAttribute("staffRole");
            if ((uri.startsWith("/admin/reports") || uri.startsWith("/admin/staff")) && !"OWNER".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return false;
            }
            return true;
        }

        // 2. Customer Routes
        if (uri.startsWith("/customer")) {
            // Public customer browsing endpoints & AI assistant
            if (uri.startsWith("/customer/catalog") || uri.startsWith("/customer/medicine") || uri.startsWith("/customer/ai")) {
                return true;
            }

            // Protected customer endpoints (Dashboard, Cart checkout, Prescriptions, Orders)
            if (session == null || session.getAttribute("customerId") == null) {
                response.sendRedirect(request.getContextPath() + "/login/customer");
                return false;
            }
            return true;
        }

        return true;
    }
}
