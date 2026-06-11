package com.quinnbank.core.cif.infrastructure;

import com.quinnbank.core.cif.domain.CustomerNumberGenerator;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Component
public class SystemClockCustomerNumberGenerator implements CustomerNumberGenerator {

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
