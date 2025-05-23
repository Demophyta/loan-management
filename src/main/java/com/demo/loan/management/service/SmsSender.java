package com.demo.loan.management.service;

public interface SmsSender {
    String sendSms(String to, String body);
}
