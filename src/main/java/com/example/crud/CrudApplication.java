package com.example.crud;

import com.example.crud.model.User;
import com.example.crud.model.enums.UserRole;
import com.example.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CrudApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(CrudApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.getByEmail("admin@gmail.com") == null){
            userRepository.save(User.builder()
                    .name("Admin")
                    .surname("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .role(UserRole.ADMIN)
                    .build());
        }
    }
}
