package com.quinnbank.core.employee.infrastructure.generator;

import com.quinnbank.core.employee.application.port.out.EmployeeNumberGeneratorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
class SystemClockEmployeeNumberGenerator implements EmployeeNumberGeneratorPort {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private final Clock clock;
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public String nextEmployeeNumber() {
        return "EMP" + LocalDate.now(clock).format(DATE_FORMAT) + "%010d".formatted(sequence.incrementAndGet());
    }
}
