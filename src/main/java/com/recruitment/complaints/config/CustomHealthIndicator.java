package com.recruitment.complaints.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return Health.up()
                        .withDetail("database", "Database connection is working")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "Database connection test failed")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "Database connection error: " + e.getMessage())
                    .build();
        }
    }
}
