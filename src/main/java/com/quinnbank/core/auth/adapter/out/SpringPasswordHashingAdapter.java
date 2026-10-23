package com.quinnbank.core.auth.adapter.out;

import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class SpringPasswordHashingAdapter implements PasswordHashingPort {
    private final Argon2PasswordEncoder passwordEncoder;
    
    public SpringPasswordHashingAdapter(Argon2PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
