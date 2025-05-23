package com.demo.loan.management.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String exception = (String) request.getAttribute("jakarta.servlet.error.exception");
        String message = (String) request.getAttribute("jakarta.servlet.error.message");

        Map<String, Object> error = new HashMap<>();
        error.put("status", statusCode != null ? statusCode : 500);
        error.put("exception", exception != null ? exception : "N/A");
        error.put("message", message != null ? message : "N/A");

        return new ResponseEntity<>(error, HttpStatus.valueOf((Integer) error.get("status")));
    }
}
