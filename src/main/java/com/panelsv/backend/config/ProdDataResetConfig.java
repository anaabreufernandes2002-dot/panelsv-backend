package com.panelsv.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

@Configuration
public class ProdDataResetConfig {

    @Bean
    CommandLineRunner resetOldProdData(JdbcTemplate jdbcTemplate, Environment environment) {
        return args -> {
            boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");

            if (!isProd) {
                return;
            }

            try {
                jdbcTemplate.execute("ALTER TABLE job_attachment ADD COLUMN IF NOT EXISTS file_url TEXT");
            } catch (Exception ignored) {}

            try {
                jdbcTemplate.execute("ALTER TABLE job_attachment ALTER COLUMN file_path DROP NOT NULL");
            } catch (Exception ignored) {}

            try {
                jdbcTemplate.execute("DELETE FROM job_note");
            } catch (Exception ignored) {}

            try {
                jdbcTemplate.execute("DELETE FROM job_attachment");
            } catch (Exception ignored) {}

            try {
                jdbcTemplate.execute("DELETE FROM job");
            } catch (Exception ignored) {}

            System.out.println("OK: prod old data reset finished");
        };
    }
}