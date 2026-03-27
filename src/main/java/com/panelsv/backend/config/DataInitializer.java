package com.panelsv.backend.config;

import com.panelsv.backend.model.Role;
import com.panelsv.backend.model.User;
import com.panelsv.backend.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner init(UserRepository users, PasswordEncoder encoder) {
        // Cria um usuário admin/admin se o banco estiver vazio
        return args -> {
            if (users.count() == 0) {
                User u = new User();
                u.setUsername("admin");
                u.setPassword(encoder.encode("admin"));
                u.setRole(Role.ADMIN);
                users.save(u);
            }
        };
    }
}
