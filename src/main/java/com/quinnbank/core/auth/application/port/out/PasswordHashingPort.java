package com.quinnbank.core.auth.application.port.out;

public interface PasswordHashingPort {
    String encode(String password);
}
