package com.quinnbank.core.account.application.command;

import com.quinnbank.core.common.domain.Money;

import java.util.UUID;

public record AccountBalanceDelta(UUID accountId, Money delta) {
}
