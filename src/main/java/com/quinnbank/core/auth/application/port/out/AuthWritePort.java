package com.quinnbank.core.auth.application.port.out;

public interface AuthWritePort {
    boolean existsByUsername(String normalizedUsername);
}
