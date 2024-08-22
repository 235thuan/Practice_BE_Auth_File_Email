package com.example.authenticationauthorization;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.authenticationauthorization.repository")
@EntityScan(basePackages = "com.example.authenticationauthorization.model")
@EnableTransactionManagement
@EnableScheduling
public class AuthenticationAuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationAuthorizationApplication.class, args);

    }

}
