package com.abhapharmacy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diagnostic Endpoint to pinpoint cloud database connectivity and configuration,
 * with one-click automated schema and data initialization over live JDBC connection.
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
            res.put("catalog", conn.getCatalog());

            // List all tables
            List<String> tables = new ArrayList<>();
            try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
            res.put("tableCount", tables.size());
            res.put("tables", tables);

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

    /**
     * Automated Direct Cloud Database Seeder
     * Executes schema.sql and data.sql directly over the authenticated SSL JDBC connection.
     */
    @GetMapping("/api/diagnostic/init-db")
    public Map<String, Object> initializeDatabase() {
        Map<String, Object> res = new HashMap<>();
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.setContinueOnError(true);
            populator.setIgnoreFailedDrops(true);
            populator.addScript(new ClassPathResource("schema.sql"));
            populator.addScript(new ClassPathResource("data.sql"));
            populator.execute(dataSource);

            res.put("status", "SUCCESS");
            res.put("message", "Schema tables and realistic demo data populated successfully into Aiven MySQL!");

            // Fetch newly created tables
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
                List<String> tables = new ArrayList<>();
                try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
                res.put("tableCount", tables.size());
                res.put("tables", tables);
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
