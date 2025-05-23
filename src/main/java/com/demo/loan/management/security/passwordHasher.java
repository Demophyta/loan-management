package com.demo.loan.management.security;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class passwordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Hashed password: " + encoder.encode("123456"));
    }
}

