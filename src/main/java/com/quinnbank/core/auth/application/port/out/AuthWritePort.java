package com.quinnbank.core.auth.application.port.out;

import com.quinnbank.core.auth.domain.model.Auth;

public interface AuthWritePort {
    boolean existsByUsername(String normalizedUsername);
    void save(Auth auth);
}
