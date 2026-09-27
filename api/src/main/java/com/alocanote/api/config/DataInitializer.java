package com.alocanote.api.config;

import com.alocanote.api.model.entity.User;
import com.alocanote.api.model.enums.Role;
import com.alocanote.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@alocanote.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .name("Administrador do Sistema")
                    .email(adminEmail)
                    .phone("83999999999")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMINISTRADOR)
                    .build();

            userRepository.save(admin);
            System.out.println("✅ Usuário ADMINISTRADOR criado com sucesso: " + adminEmail);
        } else {
            System.out.println("ℹ️ Usuário ADMINISTRADOR já existe no banco de dados.");
        }
    }
}