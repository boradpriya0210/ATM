package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "service", "repository", "config"})
public class ATMApplication {
    public static void main(String[] args) {
        SpringApplication.run(ATMApplication.class, args);
    }
}
