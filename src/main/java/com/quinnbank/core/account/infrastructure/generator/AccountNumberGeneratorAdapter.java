package com.quinnbank.core.account.infrastructure.generator;

import com.quinnbank.core.account.application.port.out.AccountNumberGeneratorPort;
import com.quinnbank.core.account.domain.model.AccountNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class AccountNumberGeneratorAdapter implements AccountNumberGeneratorPort {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private final Clock clock;
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public AccountNumber nextAccountNumber() {
        return new AccountNumber("ACCT" + LocalDate.now(clock).format(DATE_FORMAT) + "%010d".formatted(sequence.incrementAndGet()));
    }
}
