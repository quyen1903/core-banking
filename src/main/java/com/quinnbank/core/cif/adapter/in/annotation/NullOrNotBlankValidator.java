package com.quinnbank.core.cif.adapter.in.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {
    @Override
    public boolean isValid(
        String value,
        ConstraintValidatorContext context
    ) {
        return value == null || !value.isBlank();
    }

}
