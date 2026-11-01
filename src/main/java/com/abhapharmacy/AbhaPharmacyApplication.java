package com.abhapharmacy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class AbhaPharmacyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbhaPharmacyApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedOwner(JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM STAFF WHERE Email = 'owner@abhapharmacy.com'", Integer.class);
				if (count == null || count == 0) {
					jdbcTemplate.update("INSERT INTO STAFF (FirstName, LastName, Email, PasswordHash, Role, LicenseNumber, HireDate, Salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
						"Super", "Admin", "owner@abhapharmacy.com", "ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f", "OWNER", "OW-00001", "2020-01-01", 100000.00);
				}
			} catch (Exception e) {
				System.err.println("WARN [SeedOwner]: Database seeding skipped or waiting for connection: " + e.getMessage());
			}
		};
	}

}
