package com.elksandro.seunegocio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.elksandro.seunegocio.models.user.entity.User;
import com.elksandro.seunegocio.models.user.enums.Role;
import com.elksandro.seunegocio.models.user.repository.UserRepository;

@Configuration
public class AdminSeederConfig {

    private static final Logger log = LoggerFactory.getLogger(AdminSeederConfig.class);

    @Value("${ADMIN_DEFAULT_NAME:Administrador Geral}")
    private String adminName;

    @Value("${ADMIN_DEFAULT_EMAIL:admin@gmail.com}")
    private String adminEmail;

    @Value("${ADMIN_DEFAULT_PASSWORD:admin12345}")
    private String adminPassword;

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                log.info("Criando usuário Administrador Geral no banco de dados...");
                
                User admin = new User();
                admin.setName(adminName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                
                admin.setWhatsapp(""); 
                
                userRepository.save(admin);
                log.info("Administrador criado com sucesso! E-mail: {}", adminEmail);
            } else {
                log.info("O Administrador já está pronto para uso no banco de dados.");
            }
        };
    }
}