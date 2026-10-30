package com.quinnbank.core.auth.adapter.out;

import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;

import org.springframework.context.annotation.Configuration;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Configuration 
public class SpringPasswordHashingAdapter implements PasswordHashingPort {

    @Override
    public String encode(String rawPassword) {
        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        return encoder.encode(rawPassword);
    }
}
