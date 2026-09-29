package com.quinnbank.core.cif.application.command;

import java.util.UUID;

public record UpdateCustomerCommand(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone
) {}
