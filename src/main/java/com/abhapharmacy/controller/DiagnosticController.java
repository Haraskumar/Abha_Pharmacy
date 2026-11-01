package com.abhapharmacy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Diagnostic Endpoint to pinpoint cloud database connectivity and configuration.
 */
@RestController
public class DiagnosticController {

    private final DataSource dataSource;

    @Value("${spring.datasource.url:}")
    private String configuredUrl;

    @Value("${spring.datasource.username:}")
    private String configuredUser;

    public DiagnosticController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/diagnostic/db")
    public Map<String, Object> testDatabaseConnection() {
        Map<String, Object> res = new HashMap<>();
        res.put("configuredUser", configuredUser);

        if (configuredUrl != null) {
            String masked = configuredUrl.replaceAll(":[^/@:]+@", ":****@");
            res.put("configuredUrl", masked);
        }

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            res.put("status", "SUCCESS");
            res.put("databaseProduct", meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            res.put("connectedUrl", meta.getURL());

            try (ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()")) {
                if (rs.next()) {
                    res.put("tableCount", rs.getInt(1));
                }
            }
        } catch (Throwable e) {
            res.put("status", "ERROR");
            res.put("errorClass", e.getClass().getName());
            res.put("errorMessage", e.getMessage());
            if (e.getCause() != null) {
                res.put("rootCauseClass", e.getCause().getClass().getName());
                res.put("rootCauseMessage", e.getCause().getMessage());
            }
        }
        return res;
    }
}
