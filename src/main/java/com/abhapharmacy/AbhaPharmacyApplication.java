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
			Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM STAFF WHERE Email = 'owner@abhapharmacy.com'", Integer.class);
			if (count == null || count == 0) {
				jdbcTemplate.update("INSERT INTO STAFF (FirstName, LastName, Email, PasswordHash, Role, LicenseNumber, HireDate, Salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
					"Super", "Admin", "owner@abhapharmacy.com", "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f", "OWNER", "OW-00001", "2020-01-01", 100000.00);
			}
		};
	}

}
