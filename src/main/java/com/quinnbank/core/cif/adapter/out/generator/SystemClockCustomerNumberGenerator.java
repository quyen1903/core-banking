package com.quinnbank.core.cif.adapter.out.generator;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.quinnbank.core.cif.application.port.out.CustomerNumberGeneratorPort;

@Component
public class SystemClockCustomerNumberGenerator implements CustomerNumberGeneratorPort {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").withZone(ZoneOffset.UTC);

    private final Clock clock = Clock.systemUTC();

    @Override
    public String nextCustomerNumber() {
        String timestamp = FORMATTER.format(Instant.now(clock));
        String entropy = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return "CIF" + timestamp + entropy;
    }
}
